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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.img.op.MaskArea;
import org.dcm4che3.img.stream.BytesWithImageDescriptor;
import org.dcm4che3.img.stream.ImageAdapter;
import org.dcm4che3.img.stream.ImageAdapter.AdaptTransferSyntax;
import org.dcm4che3.img.util.Editable;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DataWriter;
import org.dcm4che3.net.DataWriterAdapter;
import org.dcm4che3.net.InputStreamDataWriter;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.Status;
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.backend.dicom.Defacer;
import org.karnak.backend.dicom.DicomForwardDestination;
import org.karnak.backend.dicom.ForwardDestination;
import org.karnak.backend.dicom.ForwardDicomNode;
import org.karnak.backend.dicom.Params;
import org.karnak.backend.dicom.WebForwardDestination;
import org.karnak.backend.exception.AbortException;
import org.karnak.backend.model.event.TransferMonitoringEvent;
import org.karnak.backend.model.image.TransformedPlanarImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.weasis.core.util.FileUtil;
import org.weasis.core.util.LangUtil;
import org.weasis.core.util.StreamUtil;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;
import org.weasis.dicom.util.ServiceUtil;
import org.weasis.dicom.util.ServiceUtil.ProgressStatus;
import org.weasis.dicom.util.StoreFromStreamSCU;
import org.weasis.dicom.web.DicomStowRS;
import org.weasis.dicom.web.HttpException;
import org.weasis.opencv.data.PlanarImage;
import org.weasis.core.util.annotations.Generated;

@Service
@Slf4j
@Generated()
public class ForwardService {

	private static final String ERROR_WHEN_FORWARDING = "Error when forwarding to the final destination";

	private final ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	public ForwardService(final ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void storeMultipleDestination(ForwardDicomNode forwardNode, List<ForwardDestination> destinations, Params p)
			throws IOException {
		if (destinations == null || destinations.isEmpty()) {
			throw new IllegalStateException("Cannot find destinations from " + forwardNode.toString());
		}
		// Exclude DICOMDIR
		if ("1.2.840.10008.1.3.10".equals(p.cuid())) {
			log.warn("Cannot send DICOMDIR {}", p.iuid());
			return;
		}

		// Prepare and transfer files
		Attributes attributes = new Attributes();
		List<File> files = new ArrayList<>();
		try {
			int nbDestinations = destinations.size();
			for (int i = 0; i < nbDestinations; i++) {
				prepareAndTransfer(forwardNode, p, i, destinations.get(i), attributes, nbDestinations, files);
			}
		}
		catch (IOException e) {
			log.error("Cannot connect to the final destination", e);
			throw e;
		}
		finally {
			// Force to clean the temporary bulk files
			files.forEach(file -> FileUtil.delete(file.toPath()));
		}
	}

	/**
	 * Prepare and transfer files
	 * @param fwdNode Forward node
	 * @param p Params
	 * @param index Current index
	 * @param destination Destination
	 * @param attributes Attributes
	 * @param nbDestinations Number of destinations to handle
	 * @param files Temp files to delete
	 */
	private void prepareAndTransfer(ForwardDicomNode fwdNode, Params p, int index, ForwardDestination destination,
			Attributes attributes, int nbDestinations, List<File> files) throws IOException {
		// Prepare transfer only for dicom destination
		if (destination instanceof DicomForwardDestination dicomDestination) {
			prepareTransfer(dicomDestination, p);
		}
		if (index == 0) {
			// First iteration: handle the first destination of the forward node
			Attributes attToApply = nbDestinations > 1 ? attributes : null;
			List<File> list = null;
			if (destination instanceof DicomForwardDestination dicomDestination) {
				list = transfer(fwdNode, dicomDestination, attToApply, p);
			}
			else if (destination instanceof WebForwardDestination webDestination) {
				list = transfer(fwdNode, webDestination, attToApply, p);
			}
			if (list != null) {
				files.addAll(list);
			}
		}
		else if (!attributes.isEmpty()) {
			// Other iterations: handle the remaining destinations of the forward node
			if (destination instanceof DicomForwardDestination dicomDestination) {
				transferOther(fwdNode, dicomDestination, attributes, p);
			}
			else if (destination instanceof WebForwardDestination webDestination) {
				transferOther(fwdNode, webDestination, attributes, p);
			}
		}
	}

	public static StoreFromStreamSCU prepareTransfer(DicomForwardDestination destination, Params p) throws IOException {
		String cuid = p.cuid();
		String tsuid = p.tsuid();
		String dstTsuid = destination.getOutputTransferSyntax(tsuid);
		StoreFromStreamSCU streamSCU = destination.getStreamSCU();
		streamSCU.prepareTransfer(destination.getStreamSCUService(), p.iuid(), cuid, dstTsuid);
		return streamSCU;
	}

	public List<File> transfer(ForwardDicomNode sourceNode, DicomForwardDestination destination, Attributes copy,
			Params p) throws IOException {
		StoreFromStreamSCU streamSCU = destination.getStreamSCU();
		DicomInputStream in = null;
		List<File> files;
		Attributes attributesOriginal = new Attributes();
		Attributes attributesToSend = new Attributes();
		try {
			if (!streamSCU.isReadyForDataTransfer()) {
				throw new IllegalStateException("Association not ready for transfer.");
			}
			DataWriter dataWriter;
			String cuid = p.cuid();
			String iuid = p.iuid();
			String tsuid = p.tsuid();
			var syntax = new AdaptTransferSyntax(tsuid,
					streamSCU.selectTransferSyntax(cuid, destination.getOutputTransferSyntax(tsuid)));
			List<AttributeEditor> editors = destination.getDicomEditors();
			TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
			if (copy == null && editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
				dataWriter = new InputStreamDataWriter(p.data());
				attributesToSend = new DicomInputStream(p.data()).readDataset();
				attributesOriginal.addAll(attributesToSend);
			}
			else {
				AttributeEditorContext context = new AttributeEditorContext(tsuid, sourceNode,
						streamSCU.getRemoteDicomNode());
				in = new DicomInputStream(p.data(), tsuid);
				in.setIncludeBulkData(IncludeBulkData.URI);
				Attributes attributes = in.readDataset();
				attributesOriginal.addAll(attributes);
				attributesToSend = attributes;
				if (copy != null) {
					copy.addAll(attributes);
				}

				if (!editors.isEmpty()) {
					editors.forEach(e -> e.apply(attributes, context));
					iuid = attributes.getString(Tag.SOPInstanceUID);
					cuid = attributes.getString(Tag.SOPClassUID);
				}

				if (context.getAbort() == Abort.FILE_EXCEPTION) {
					if (p.data() instanceof PDVInputStream) {
						((PDVInputStream) p.data()).skipAll();
					}
					throw new AbortException(context.getAbort(), context.getAbortMessage());
				}
				else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
					if (p.as() != null) {
						p.as().abort();
					}
					throw new AbortException(context.getAbort(),
							"DICOM association abort: " + context.getAbortMessage());
				}
				dataWriter = buildDataWriterFromTransformedImage(syntax, context, attributes, transformedPlanarImage);
			}

			launchCStore(p, streamSCU, dataWriter, cuid, iuid, syntax, transformedPlanarImage);

			progressNotify(destination, p.iuid(), p.cuid(), false, streamSCU);
			monitor(sourceNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, false, null,
					attributesOriginal.getString(Tag.Modality), p.cuid());
		}
		catch (AbortException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(sourceNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, false,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
				throw e;
			}
		}
		catch (IOException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(sourceNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			throw e;
		}
		catch (Exception e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(sourceNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			log.error(ERROR_WHEN_FORWARDING, e);
		}
		finally {
			streamSCU.triggerCloseExecutor();
			files = cleanOrGetBulkDataFiles(in, copy == null);
		}
		return files;
	}

	private void launchCStore(Params p, StoreFromStreamSCU streamSCU, DataWriter dataWriter, String cuid, String iuid,
			AdaptTransferSyntax syntax, TransformedPlanarImage transformedPlanarImage)
			throws IOException, InterruptedException {
		try {
			streamSCU.cstore(cuid, iuid, p.priority(), dataWriter, syntax.getSuitable());
		}
		finally {
			if (transformedPlanarImage != null && transformedPlanarImage.getPlanarImage() != null) {
				transformedPlanarImage.getPlanarImage().release();
			}
		}
	}

	private DataWriter buildDataWriterFromTransformedImage(AdaptTransferSyntax syntax, AttributeEditorContext context,
			Attributes attributes, TransformedPlanarImage transformedPlanarImage) throws IOException {
		DataWriter dataWriter;
		BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
		transformedPlanarImage = transformImage(attributes, context, transformedPlanarImage);
		dataWriter = ImageAdapter.buildDataWriter(attributes, syntax,
				transformedPlanarImage != null ? transformedPlanarImage.getEditablePlanarImage() : null, desc);
		return dataWriter;
	}

	private static TransformedPlanarImage transformImage(Attributes attributes, AttributeEditorContext context,
			TransformedPlanarImage transformedPlanarImage) {
		MaskArea m = context.getMaskArea();
		boolean defacing = LangUtil.emptyToFalse(context.getProperties().getProperty(Defacer.APPLY_DEFACING));
		if (m != null || defacing) {
			Editable<PlanarImage> editablePlanarImage = buildEditablePlanarImage(attributes, m, defacing,
					transformedPlanarImage);
			transformedPlanarImage.setEditablePlanarImage(editablePlanarImage);
			return transformedPlanarImage;
		}
		return null;
	}

	private static Editable<PlanarImage> buildEditablePlanarImage(Attributes attributes, MaskArea m, boolean defacing,
			TransformedPlanarImage transformedPlanarImage) {
		return img -> {
			PlanarImage planarImage = buildPlanarImage(attributes, m, defacing, img);
			transformedPlanarImage.setPlanarImage(planarImage);
			return planarImage;
		};
	}

	private static PlanarImage buildPlanarImage(Attributes attributes, MaskArea m, boolean defacing, PlanarImage img) {
		PlanarImage image = img;
		if (defacing) {
			image = Defacer.apply(attributes, image);
		}
		if (m != null) {
			image = MaskArea.drawShape(image.toMat(), m);
		}
		return image;
	}

	private static List<File> cleanOrGetBulkDataFiles(DicomInputStream in, boolean clean) {
		StreamUtil.safeClose(in);
		if (clean) {
			// Force to clean if tmp bulk files
			ServiceUtil.safeClose(in);
		}
		else if (in != null) {
			// Return tmp bulk files
			return in.getBulkDataFiles();
		}
		return null;
	}

	public void transferOther(ForwardDicomNode fwdNode, DicomForwardDestination destination, Attributes copy, Params p)
			throws IOException {
		StoreFromStreamSCU streamSCU = destination.getStreamSCU();
		Attributes attributesToSend = new Attributes();
		Attributes attributesOriginal = new Attributes();
		try {
			if (!streamSCU.isReadyForDataTransfer()) {
				throw new IllegalStateException("Association not ready for transfer.");
			}

			DataWriter dataWriter;
			String tsuid = p.tsuid();
			String iuid = p.iuid();
			String cuid = p.cuid();
			var syntax = new AdaptTransferSyntax(tsuid,
					streamSCU.selectTransferSyntax(cuid, destination.getOutputTransferSyntax(tsuid)));
			List<AttributeEditor> editors = destination.getDicomEditors();
			TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
			if (editors.isEmpty() && syntax.getRequested().equals(tsuid)) {
				dataWriter = new DataWriterAdapter(copy);
				attributesOriginal.addAll(copy);
				attributesToSend = copy;
			}
			else {
				AttributeEditorContext context = new AttributeEditorContext(tsuid, fwdNode,
						streamSCU.getRemoteDicomNode());
				Attributes attributes = new Attributes(copy);
				attributesOriginal.addAll(attributes);
				attributesToSend = attributes;
				if (!editors.isEmpty()) {
					editors.forEach(e -> e.apply(attributes, context));
					iuid = attributes.getString(Tag.SOPInstanceUID);
					cuid = attributes.getString(Tag.SOPClassUID);
				}

				if (context.getAbort() == Abort.FILE_EXCEPTION) {
					throw new AbortException(context.getAbort(), context.getAbortMessage());
				}
				else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
					throw new AbortException(context.getAbort(),
							"DICOM association abort. " + context.getAbortMessage());
				}
				dataWriter = buildDataWriterFromTransformedImage(syntax, context, attributes, transformedPlanarImage);
			}

			launchCStore(p, streamSCU, dataWriter, cuid, iuid, syntax, transformedPlanarImage);

			progressNotify(destination, p.iuid(), p.cuid(), false, streamSCU);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, false, null,
					attributesOriginal.getString(Tag.Modality), p.cuid());
		}
		catch (AbortException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, false,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
				throw e;
			}
		}
		catch (IOException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			throw e;
		}
		catch (Exception e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			progressNotify(destination, p.iuid(), p.cuid(), true, streamSCU);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			log.error(ERROR_WHEN_FORWARDING, e);
		}
		finally {
			streamSCU.triggerCloseExecutor();
		}
	}

	public List<File> transfer(ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p)
			throws IOException {
		DicomInputStream in = null;
		List<File> files;
		Attributes attributesToSend = new Attributes();
		Attributes attributesOriginal = new Attributes();
		try {
			List<AttributeEditor> editors = destination.getDicomEditors();
			DicomStowRS stow = destination.getStowrsSingleFile();
			var syntax = new AdaptTransferSyntax(p.tsuid(), destination.getOutputTransferSyntax(p.tsuid()));

			if (syntax.getRequested().equals(p.tsuid()) && copy == null && editors.isEmpty()) {
				Attributes fmi = Attributes.createFileMetaInformation(p.iuid(), p.cuid(), syntax.getRequested());
				try (InputStream stream = p.data()) {
					attributesToSend = new DicomInputStream(p.data()).readDataset();
					attributesOriginal.addAll(attributesToSend);
					stow.uploadDicom(stream, fmi);
				}
				catch (HttpException httpException) {
					if (httpException.getStatusCode() != 409) {
						throw new AbortException(Abort.FILE_EXCEPTION, httpException.getMessage());
					}
					else {
						log.debug("File already present in destination");
					}
				}
			}
			else {
				AttributeEditorContext context = new AttributeEditorContext(p.tsuid(), fwdNode, null);
				in = new DicomInputStream(p.data(), p.tsuid());
				in.setIncludeBulkData(IncludeBulkData.URI);
				Attributes attributes = in.readDataset();
				attributesToSend = attributes;
				attributesOriginal.addAll(attributes);
				if (copy != null) {
					copy.addAll(attributes);
				}
				if (!editors.isEmpty()) {
					editors.forEach(e -> e.apply(attributes, context));
				}

				if (context.getAbort() == Abort.FILE_EXCEPTION) {
					if (p.data() instanceof PDVInputStream) {
						((PDVInputStream) p.data()).skipAll();
					}
					throw new AbortException(context.getAbort(), context.getAbortMessage());
				}
				else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
					if (p.as() != null) {
						p.as().abort();
					}
					throw new AbortException(context.getAbort(), "STOW-RS abort: " + context.getAbortMessage());
				}

				BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
				if (desc == null) {
					stow.uploadDicom(attributes, syntax.getOriginal());
				}
				else {
					uploadPayLoadFromTransformedImage(stow, syntax, context, attributes, desc);
				}
			}
			progressNotify(destination, p.iuid(), p.cuid(), false, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, false, null,
					attributesOriginal.getString(Tag.Modality), p.cuid());
		}
		catch (AbortException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, false,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
				throw e;
			}
		}
		catch (IOException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			throw e;
		}
		catch (Exception e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			log.error(ERROR_WHEN_FORWARDING, e);
		}
		finally {
			files = cleanOrGetBulkDataFiles(in, copy == null);
		}
		return files;
	}

	private void uploadPayLoadFromTransformedImage(DicomStowRS stow, AdaptTransferSyntax syntax,
			AttributeEditorContext context, Attributes attributes, BytesWithImageDescriptor desc) throws Exception {
		TransformedPlanarImage transformedPlanarImage = new TransformedPlanarImage();
		try {
			transformedPlanarImage = transformImage(attributes, context, transformedPlanarImage);
			if (transformedPlanarImage == null) {
				throw new IllegalStateException("Cannot transcode image for STOW-RS upload.");
			}
			stow.uploadPayload(DicomStowRS.createCompressedImagePayload(attributes, syntax, desc,
					transformedPlanarImage.getEditablePlanarImage()));
		}
		finally {
			if (transformedPlanarImage != null && transformedPlanarImage.getPlanarImage() != null) {
				transformedPlanarImage.getPlanarImage().release();
			}
		}
	}

	public void transferOther(ForwardDicomNode fwdNode, WebForwardDestination destination, Attributes copy, Params p)
			throws IOException {
		Attributes attributesToSend = new Attributes();
		Attributes attributesOriginal = new Attributes();
		try {
			List<AttributeEditor> editors = destination.getDicomEditors();
			DicomStowRS stow = destination.getStowrsSingleFile();
			var syntax = new AdaptTransferSyntax(p.tsuid(), destination.getOutputTransferSyntax(p.tsuid()));
			if (syntax.getRequested().equals(p.tsuid()) && editors.isEmpty()) {
				attributesToSend = copy;
				attributesOriginal.addAll(copy);
				stow.uploadDicom(copy, syntax.getRequested());
			}
			else {
				AttributeEditorContext context = new AttributeEditorContext(p.tsuid(), fwdNode, null);
				Attributes attributes = new Attributes(copy);
				attributesToSend = attributes;
				attributesOriginal.addAll(attributes);
				editors.forEach(e -> e.apply(attributes, context));

				if (context.getAbort() == Abort.FILE_EXCEPTION) {
					throw new AbortException(context.getAbort(), context.getAbortMessage());
				}
				else if (context.getAbort() == Abort.CONNECTION_EXCEPTION) {
					throw new AbortException(context.getAbort(),
							"DICOM associtation abort. " + context.getAbortMessage());
				}

				BytesWithImageDescriptor desc = ImageAdapter.imageTranscode(attributes, syntax, context);
				if (desc == null) {
					stow.uploadDicom(attributes, syntax.getOriginal());
				}
				else {
					uploadPayLoadFromTransformedImage(stow, syntax, context, attributes, desc);
				}
				progressNotify(destination, p.iuid(), p.cuid(), false, 0);
				monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, false, null,
						attributesOriginal.getString(Tag.Modality), p.cuid());
			}
		}
		catch (HttpException httpException) {
			if (httpException.getStatusCode() != 409) {
				progressNotify(destination, p.iuid(), p.cuid(), true, 0);
				monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
						httpException.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
				throw new AbortException(Abort.FILE_EXCEPTION, "DICOMWeb forward", httpException);
			}
			else {
				progressNotify(destination, p.iuid(), p.cuid(), false, 0);
				monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, true, true, null,
						attributesOriginal.getString(Tag.Modality), p.cuid());
				log.debug("File already present in destination");
			}
		}
		catch (AbortException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, false,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			if (e.getAbort() == Abort.CONNECTION_EXCEPTION) {
				throw e;
			}
		}
		catch (IOException e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			throw e;
		}
		catch (Exception e) {
			progressNotify(destination, p.iuid(), p.cuid(), true, 0);
			monitor(fwdNode.getId(), destination.getId(), attributesOriginal, attributesToSend, false, true,
					e.getMessage(), attributesOriginal.getString(Tag.Modality), p.cuid());
			log.error(ERROR_WHEN_FORWARDING, e);
		}
	}

	private static void progressNotify(ForwardDestination destination, String iuid, String cuid, boolean failed,
			StoreFromStreamSCU streamSCU) {
		streamSCU.removeIUIDProcessed(iuid);
		ServiceUtil.notifyProgression(destination.getState(), iuid, cuid,
				failed ? Status.ProcessingFailure : Status.Success,
				failed ? ProgressStatus.FAILED : ProgressStatus.COMPLETED, streamSCU.getNumberOfSuboperations());
	}

	private static void progressNotify(ForwardDestination destination, String iuid, String cuid, boolean failed,
			int subOperations) {
		ServiceUtil.notifyProgression(destination.getState(), iuid, cuid,
				failed ? Status.ProcessingFailure : Status.Success,
				failed ? ProgressStatus.FAILED : ProgressStatus.COMPLETED, subOperations);
	}

	public static String selectTransferSyntax(Association as, String cuid, String filets) {
		Set<String> tss = as.getTransferSyntaxesFor(cuid);
		if (tss.contains(filets)) {
			return filets;
		}

		if (tss.contains(UID.ExplicitVRLittleEndian)) {
			return UID.ExplicitVRLittleEndian;
		}

		return UID.ImplicitVRLittleEndian;
	}

	/**
	 * Publish an event for monitoring purpose
	 * @param forwardNodeId ForwardNode Id
	 * @param destinationId Destination Id
	 * @param attributesOriginal Original value
	 * @param attributesToSend De-identify value
	 * @param sent Flag to know if the transfer occurred
	 * @param reason Reason of not transferring the file
	 * @param modality Modality of the file transferred
	 * @param sopClassUid Sop Class Uid
	 */
	private void monitor(Long forwardNodeId, Long destinationId, Attributes attributesOriginal,
			Attributes attributesToSend, boolean sent, boolean error, String reason, String modality,
			String sopClassUid) {
		applicationEventPublisher
			.publishEvent(new TransferMonitoringEvent(TransferStatusEntity.buildTransferStatusEntity(forwardNodeId,
					destinationId, attributesOriginal, attributesToSend, sent, error, reason, modality, sopClassUid)));
	}

}
