/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.backend.service.DicomNodeConfigService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weasis.dicom.param.DicomNode;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class DicomNodeUtilTest {

	@Mock
	private DicomNodeConfigService dicomNodeConfigService;

	@InjectMocks
	private DicomNodeUtil dicomNodeUtil;

	@Test
	void returns_all_dicom_node_types_from_service() {
		var workstations = new DicomNodeList("Workstations");
		var pacsWeb = new DicomNodeList("PACS Public WEB");
		when(dicomNodeConfigService.getAllDicomNodeTypes()).thenReturn(List.of(workstations, pacsWeb));

		var result = dicomNodeUtil.getAllDicomNodeTypes();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Workstations", result.get(0).getName());
		assertEquals("PACS Public WEB", result.get(1).getName());
	}

	@Test
	void returns_worklist_nodes_from_service() {
		var worklists = new DicomNodeList("Worklists");
		when(dicomNodeConfigService.getWorkListNodes()).thenReturn(worklists);

		var result = dicomNodeUtil.getWorkListNodes();

		assertNotNull(result);
		assertEquals("Worklists", result.getName());
	}

	@Test
	void delegates_save_dicom_node_to_service() {
		var node = new ConfigNode("PACS", new DicomNode("PACS", "pacs.host", 104));
		when(dicomNodeConfigService.saveNode("PACS", "PACS", "pacs.host", 104, "WORKSTATION")).thenReturn(node);

		var result = dicomNodeUtil.saveDicomNode("PACS", "PACS", "pacs.host", 104, "WORKSTATION");

		assertSame(node, result);
	}

	@Test
	void delegates_update_dicom_node_to_service() {
		var node = new ConfigNode("PACS", new DicomNode("PACS", "pacs.host", 104));
		when(dicomNodeConfigService.updateNode(1L, "PACS", "PACS", "pacs.host", 104)).thenReturn(node);

		var result = dicomNodeUtil.updateDicomNode(1L, "PACS", "PACS", "pacs.host", 104);

		assertSame(node, result);
	}

	@Test
	void delegates_delete_dicom_node_to_service() {
		dicomNodeUtil.deleteDicomNode(9L);

		verify(dicomNodeConfigService).deleteNode(9L);
	}

	@Test
	void delegates_get_groups_to_service() {
		when(dicomNodeConfigService.getGroups()).thenReturn(List.of("WORKSTATION", "PACS_WEB"));

		assertEquals(List.of("WORKSTATION", "PACS_WEB"), dicomNodeUtil.getGroups());
	}

	@Test
	void delegates_create_group_to_service() {
		dicomNodeUtil.createGroup("Workstations");

		verify(dicomNodeConfigService).createGroup("Workstations");
	}

	@Test
	void delegates_rename_group_to_service() {
		dicomNodeUtil.renameGroup("OLD", "NEW");

		verify(dicomNodeConfigService).renameGroup("OLD", "NEW");
	}

	@Test
	void delegates_delete_group_to_service() {
		when(dicomNodeConfigService.deleteGroup("PACS_WEB")).thenReturn(2);

		assertEquals(2, dicomNodeUtil.deleteGroup("PACS_WEB"));
	}

	@Test
	void delegates_export_dicom_nodes_to_service() {
		byte[] csv = "csv".getBytes();
		when(dicomNodeConfigService.exportDicomNodes()).thenReturn(csv);

		assertSame(csv, dicomNodeUtil.exportDicomNodes());
	}

	@Test
	void delegates_export_worklist_nodes_to_service() {
		byte[] csv = "csv".getBytes();
		when(dicomNodeConfigService.exportWorkListNodes()).thenReturn(csv);

		assertSame(csv, dicomNodeUtil.exportWorkListNodes());
	}

	@Test
	void delegates_import_dicom_nodes_to_service() {
		var stream = new ByteArrayInputStream(new byte[0]);
		when(dicomNodeConfigService.importDicomNodes(stream, ',')).thenReturn(3);

		assertEquals(3, dicomNodeUtil.importDicomNodes(stream, ','));
	}

	@Test
	void delegates_import_worklist_nodes_to_service() {
		var stream = new ByteArrayInputStream(new byte[0]);
		when(dicomNodeConfigService.importWorkListNodes(stream, ',')).thenReturn(1);

		assertEquals(1, dicomNodeUtil.importWorkListNodes(stream, ','));
	}

}
