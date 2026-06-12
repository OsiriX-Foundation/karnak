/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TransferStatusEntityTest {

	private static final LocalDateTime DATE = LocalDateTime.of(2024, 1, 1, 12, 0);

	@Test
	void exposes_every_property_through_setters() {
		TransferStatusEntity entity = new TransferStatusEntity();
		ForwardNodeEntity forwardNode = new ForwardNodeEntity();
		DestinationEntity destination = new DestinationEntity();
		entity.setId(1L);
		entity.setForwardNodeEntity(forwardNode);
		entity.setForwardNodeId(2L);
		entity.setDestinationEntity(destination);
		entity.setDestinationId(3L);
		entity.setTransferDate(DATE);
		entity.setSent(true);
		entity.setError(true);
		entity.setReason("reason");
		entity.setPatientIdOriginal("PID");
		entity.setAccessionNumberOriginal("ACC");
		entity.setStudyDescriptionOriginal("STD");
		entity.setStudyDateOriginal(DATE);
		entity.setStudyUidOriginal("study-uid");
		entity.setSerieDescriptionOriginal("SER");
		entity.setSerieDateOriginal(DATE);
		entity.setSerieUidOriginal("serie-uid");
		entity.setSopInstanceUidOriginal("sop-uid");
		entity.setPatientIdToSend("PID2");
		entity.setAccessionNumberToSend("ACC2");
		entity.setStudyDescriptionToSend("STD2");
		entity.setStudyDateToSend(DATE);
		entity.setStudyUidToSend("study-uid2");
		entity.setSerieDescriptionToSend("SER2");
		entity.setSerieDateToSend(DATE);
		entity.setSerieUidToSend("serie-uid2");
		entity.setSopInstanceUidToSend("sop-uid2");
		entity.setModality("CT");
		entity.setSopClassUid("1.2.840.10008.5.1.4.1.1.2");

		assertEquals(1L, entity.getId());
		assertEquals(forwardNode, entity.getForwardNodeEntity());
		assertEquals(2L, entity.getForwardNodeId());
		assertEquals(destination, entity.getDestinationEntity());
		assertEquals(3L, entity.getDestinationId());
		assertEquals(DATE, entity.getTransferDate());
		assertTrue(entity.isSent());
		assertTrue(entity.isError());
		assertEquals("reason", entity.getReason());
		assertEquals("PID", entity.getPatientIdOriginal());
		assertEquals("ACC", entity.getAccessionNumberOriginal());
		assertEquals("STD", entity.getStudyDescriptionOriginal());
		assertEquals(DATE, entity.getStudyDateOriginal());
		assertEquals("study-uid", entity.getStudyUidOriginal());
		assertEquals("SER", entity.getSerieDescriptionOriginal());
		assertEquals(DATE, entity.getSerieDateOriginal());
		assertEquals("serie-uid", entity.getSerieUidOriginal());
		assertEquals("sop-uid", entity.getSopInstanceUidOriginal());
		assertEquals("PID2", entity.getPatientIdToSend());
		assertEquals("ACC2", entity.getAccessionNumberToSend());
		assertEquals("STD2", entity.getStudyDescriptionToSend());
		assertEquals(DATE, entity.getStudyDateToSend());
		assertEquals("study-uid2", entity.getStudyUidToSend());
		assertEquals("SER2", entity.getSerieDescriptionToSend());
		assertEquals(DATE, entity.getSerieDateToSend());
		assertEquals("serie-uid2", entity.getSerieUidToSend());
		assertEquals("sop-uid2", entity.getSopInstanceUidToSend());
		assertEquals("CT", entity.getModality());
		assertEquals("1.2.840.10008.5.1.4.1.1.2", entity.getSopClassUid());
	}

	@Test
	void all_arguments_constructor_populates_the_status() {
		TransferStatusEntity entity = new TransferStatusEntity(1L, 2L, DATE, true, false, "reason", "PID", "ACC", "STD",
				DATE, "study-uid", "SER", DATE, "serie-uid", "sop-uid", "PID2", "ACC2", "STD2", DATE, "study-uid2",
				"SER2", DATE, "serie-uid2", "sop-uid2", "CT", "1.2.3");

		assertEquals(1L, entity.getForwardNodeId());
		assertEquals(2L, entity.getDestinationId());
		assertTrue(entity.isSent());
		assertFalse(entity.isError());
		assertEquals("PID", entity.getPatientIdOriginal());
		assertEquals("PID2", entity.getPatientIdToSend());
		assertEquals("CT", entity.getModality());
	}

	@Test
	void build_transfer_status_entity_reads_the_dicom_attributes() {
		Attributes original = new Attributes();
		original.setString(Tag.PatientID, VR.LO, "PID-ORIG");
		original.setString(Tag.StudyInstanceUID, VR.UI, "1.2.3");
		Attributes toSend = new Attributes();
		toSend.setString(Tag.PatientID, VR.LO, "PID-SEND");

		TransferStatusEntity entity = TransferStatusEntity.buildTransferStatusEntity(1L, 2L, original, toSend, true,
				false, "ok", "CT", "1.2.840.10008.5.1.4.1.1.2");

		assertEquals("PID-ORIG", entity.getPatientIdOriginal());
		assertEquals("1.2.3", entity.getStudyUidOriginal());
		assertEquals("PID-SEND", entity.getPatientIdToSend());
		assertEquals("CT", entity.getModality());
		assertTrue(entity.isSent());
	}

	@Test
	void equal_instances_match_and_share_a_hash() {
		TransferStatusEntity a = status(1L, "PID");
		TransferStatusEntity b = status(1L, "PID");

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a, a);
	}

	@Test
	void differs_by_field_type_or_null() {
		TransferStatusEntity base = status(1L, "PID");

		assertNotEquals(base, status(2L, "PID"));
		assertNotEquals(base, status(1L, "OTHER"));
		assertNotEquals("not-a-status", base);
		assertFalse(base.equals(null));
	}

	@Test
	void to_string_contains_the_identifiers() {
		assertTrue(status(1L, "PID").toString().contains("patientIdOriginal='PID'"));
	}

	private static TransferStatusEntity status(Long id, String patientId) {
		TransferStatusEntity entity = new TransferStatusEntity();
		entity.setId(id);
		entity.setPatientIdOriginal(patientId);
		return entity;
	}

}