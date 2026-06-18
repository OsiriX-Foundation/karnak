/*
 * Copyright (c) 2009-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.karnak.backend.dicom.DicomForwardDestination;
import org.karnak.backend.dicom.ForwardDicomNode;
import org.karnak.backend.dicom.Params;
import org.springframework.context.ApplicationEventPublisher;
import org.weasis.dicom.param.AdvancedParams;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.ConnectOptions;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.ListenerParams;
import org.weasis.dicom.tool.DicomListener;

/**
 * End-to-end forwarding test: a real DICOM Store SCP (the destination PACS) is started on
 * a loopback port, and instances are forwarded to it through {@link ForwardService} with
 * a multi-association connection pool. It checks that every instance actually arrives —
 * exercising real associations, the lease/return pool, and concurrent sends.
 */
@org.junit.jupiter.api.Tag("integration")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ForwardServiceIntegrationTest {

	private static final String TS = UID.ExplicitVRLittleEndian;

	private static final String CUID = UID.SecondaryCaptureImageStorage;

	@TempDir
	private Path storageDir;

	private DicomListener listener;

	private ForwardDicomNode fwdNode;

	private DicomNode destNode;

	private ForwardService forwardService;

	@BeforeEach
	void setUp() throws Exception {
		int port = freePort();
		String scpAet = "KARNAK-IT-SCP";
		listener = new DicomListener(storageDir);
		listener.start(new DicomNode(scpAet, null, port), new ListenerParams(true));

		fwdNode = new ForwardDicomNode("SOURCE-IT");
		destNode = new DicomNode(scpAet, "localhost", port);
		forwardService = new ForwardService(mock(ApplicationEventPublisher.class));
	}

	@AfterEach
	void tearDown() {
		if (listener != null) {
			listener.stop();
		}
	}

	@Test
	void forwards_every_instance_sequentially_over_a_single_connection() throws Exception {
		Set<String> expected = forwardInstances(1, 25, false);
		assertEquals(expected, receivedSopInstanceUids());
	}

	@Test
	void forwards_every_instance_concurrently_through_the_pool() throws Exception {
		Set<String> expected = forwardInstances(3, 40, true);
		assertEquals(expected, receivedSopInstanceUids());
	}

	/**
	 * Forwards {@code count} distinct instances to a destination whose pool holds
	 * {@code poolSize} associations, either sequentially or from a small thread pool.
	 * Returns the SOP Instance UIDs that were sent. The destination is stopped at the
	 * end, which drains outstanding responses and closes the associations, so all
	 * received files are on disk when this returns.
	 */
	private Set<String> forwardInstances(int poolSize, int count, boolean concurrent) throws Exception {
		// Mirror production: the gateway always adds at least one editor (a
		// StreamRegistryEditor), so
		// the forward goes through the dataset-reading send path. A no-op editor stands
		// in for it.
		AttributeEditor noop = (attributes, context) -> {
		};
		DicomForwardDestination dest = new DicomForwardDestination(1L, advancedParams(), fwdNode, destNode, false, null,
				List.of(noop), null, true, poolSize);
		Set<String> sent = new TreeSet<>();
		try {
			List<Callable<Void>> jobs = new java.util.ArrayList<>();
			for (int i = 0; i < count; i++) {
				String iuid = "1.2.826.0.1.3680043.8.498." + i;
				sent.add(iuid);
				byte[] object = serialize(iuid);
				jobs.add(() -> {
					Params p = new Params(iuid, CUID, TS, 0, new ByteArrayInputStream(object), null);
					forwardService.storeMultipleDestination(fwdNode, List.of(dest), p);
					return null;
				});
			}
			if (concurrent) {
				runConcurrently(jobs);
			}
			else {
				for (Callable<Void> job : jobs) {
					job.call();
				}
			}
		}
		finally {
			// Forces the associations closed and waits for outstanding C-STORE responses.
			dest.stop();
		}
		return sent;
	}

	private static void runConcurrently(List<Callable<Void>> jobs) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(6);
		try {
			List<Future<Void>> futures = pool.invokeAll(jobs);
			for (Future<Void> future : futures) {
				future.get(30, TimeUnit.SECONDS);
			}
		}
		finally {
			pool.shutdownNow();
		}
	}

	/**
	 * SOP Instance UIDs of every DICOM file the SCP wrote under the storage directory.
	 */
	private Set<String> receivedSopInstanceUids() throws IOException {
		Set<String> received = new TreeSet<>();
		try (Stream<Path> files = Files.walk(storageDir)) {
			for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
				try (DicomInputStream dis = new DicomInputStream(file.toFile())) {
					received.add(dis.readDataset().getString(Tag.SOPInstanceUID));
				}
			}
		}
		return received;
	}

	private static AdvancedParams advancedParams() {
		AdvancedParams params = new AdvancedParams();
		ConnectOptions connectOptions = new ConnectOptions();
		connectOptions.setConnectTimeout(5000);
		connectOptions.setAcceptTimeout(7000);
		connectOptions.setMaxOpsInvoked(16);
		connectOptions.setMaxOpsPerformed(16);
		params.setConnectOptions(connectOptions);
		return params;
	}

	/**
	 * A minimal Secondary Capture object serialized as a raw dataset (no file-meta), as
	 * the C-STORE SCP delivers it to the forwarding pipeline through a
	 * {@code PDVInputStream}.
	 */
	private static byte[] serialize(String iuid) throws IOException {
		Attributes data = new Attributes();
		data.setString(Tag.SOPClassUID, VR.UI, CUID);
		data.setString(Tag.SOPInstanceUID, VR.UI, iuid);
		data.setString(Tag.StudyInstanceUID, VR.UI, "1.2.826.0.1.3680043.8.498.100");
		data.setString(Tag.SeriesInstanceUID, VR.UI, "1.2.826.0.1.3680043.8.498.200");
		data.setString(Tag.PatientID, VR.LO, "IT-PATIENT");
		data.setString(Tag.PatientName, VR.PN, "Integration^Test");
		data.setString(Tag.Modality, VR.CS, "OT");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DicomOutputStream dos = new DicomOutputStream(baos, TS)) {
			dos.writeDataset(null, data);
		}
		return baos.toByteArray();
	}

	private static int freePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}

}