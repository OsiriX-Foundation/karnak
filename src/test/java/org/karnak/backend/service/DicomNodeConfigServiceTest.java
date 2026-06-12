/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karnak.backend.data.entity.DicomNodeConfigEntity;
import org.karnak.backend.data.entity.DicomNodeGroupEntity;
import org.karnak.backend.data.repo.DicomNodeConfigRepo;
import org.karnak.backend.data.repo.DicomNodeGroupRepo;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class DicomNodeConfigServiceTest {

	@Mock
	private DicomNodeConfigRepo dicomNodeConfigRepo;

	@Mock
	private DicomNodeGroupRepo dicomNodeGroupRepo;

	@InjectMocks
	private DicomNodeConfigService dicomNodeConfigService;

	@Test
	void returns_one_list_per_group_named_after_the_group() {
		when(dicomNodeGroupRepo.findAllByOrderByNameAsc())
			.thenReturn(List.of(new DicomNodeGroupEntity("PACS_WEB"), new DicomNodeGroupEntity("WORKSTATION")));
		var workstation = new DicomNodeConfigEntity("Karnak", "KARNAK-GATEWAY", "localhost", 11112, "WORKSTATION");
		var pacsNode = new DicomNodeConfigEntity("Public dicomserver", "DICOMSERVER", "dicomserver.co.uk", 11112,
				"PACS_WEB");

		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of(workstation));
		when(dicomNodeConfigRepo.findByNodeType("PACS_WEB")).thenReturn(List.of(pacsNode));

		var result = dicomNodeConfigService.getAllDicomNodeTypes();

		// ordered by group name and named after the group (no more fixed display names)
		assertEquals(2, result.size());
		assertEquals("PACS_WEB", result.get(0).getName());
		assertEquals("WORKSTATION", result.get(1).getName());
		assertEquals(1, result.get(1).size());
		assertEquals("Karnak", result.get(1).getFirst().getName());
		assertEquals("KARNAK-GATEWAY", result.get(1).getFirst().getAet());
	}

	@Test
	void returns_empty_lists_when_groups_have_no_nodes() {
		when(dicomNodeGroupRepo.findAllByOrderByNameAsc())
			.thenReturn(List.of(new DicomNodeGroupEntity("PACS_WEB"), new DicomNodeGroupEntity("WORKSTATION")));
		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of());
		when(dicomNodeConfigRepo.findByNodeType("PACS_WEB")).thenReturn(List.of());

		var result = dicomNodeConfigService.getAllDicomNodeTypes();

		assertEquals(2, result.size());
		assertTrue(result.get(0).isEmpty());
		assertTrue(result.get(1).isEmpty());
	}

	@Test
	void returns_worklist_nodes() {
		var worklist = new DicomNodeConfigEntity("Public dicomserver", "ADVT", "dicomserver.co.uk", 104, "WORKLIST");

		when(dicomNodeConfigRepo.findByNodeType("WORKLIST")).thenReturn(List.of(worklist));

		var result = dicomNodeConfigService.getWorkListNodes();

		assertNotNull(result);
		assertEquals("Worklists", result.getName());
		assertEquals(1, result.size());
		assertEquals("ADVT", result.getFirst().getAet());
		assertEquals("dicomserver.co.uk", result.getFirst().getHostname());
		assertEquals(104, result.getFirst().getPort());
	}

	@Test
	void exposes_id_and_node_type_on_loaded_nodes() {
		var workstation = new DicomNodeConfigEntity("Karnak", "KARNAK-GATEWAY", "localhost", 11112, "WORKSTATION");
		workstation.setId(42L);

		when(dicomNodeGroupRepo.findAllByOrderByNameAsc()).thenReturn(List.of(new DicomNodeGroupEntity("WORKSTATION")));
		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of(workstation));

		var node = dicomNodeConfigService.getAllDicomNodeTypes().get(0).getFirst();

		assertEquals(42L, node.getId());
		assertEquals("WORKSTATION", node.getNodeType());
	}

	@Test
	void save_node_persists_entity_and_returns_config_node() {
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class))).thenAnswer(invocation -> {
			DicomNodeConfigEntity entity = invocation.getArgument(0);
			entity.setId(7L);
			return entity;
		});

		var saved = dicomNodeConfigService.saveNode("My PACS", "PACS", "pacs.host", 104, "WORKSTATION");

		var captor = ArgumentCaptor.forClass(DicomNodeConfigEntity.class);
		verify(dicomNodeConfigRepo).save(captor.capture());
		var persisted = captor.getValue();
		assertEquals("My PACS", persisted.getDescription());
		assertEquals("PACS", persisted.getAeTitle());
		assertEquals("pacs.host", persisted.getHostname());
		assertEquals(104, persisted.getPort());
		assertEquals("WORKSTATION", persisted.getNodeType());

		assertEquals(7L, saved.getId());
		assertEquals("My PACS", saved.getName());
		assertEquals("WORKSTATION", saved.getNodeType());
	}

	@Test
	void save_node_falls_back_to_ae_title_when_description_is_blank() {
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		var saved = dicomNodeConfigService.saveNode("  ", "PACS", "pacs.host", 104, "WORKSTATION");

		assertEquals("PACS", saved.getName());
	}

	@Test
	void update_node_mutates_existing_entity() {
		var existing = new DicomNodeConfigEntity("Old", "OLD", "old.host", 100, "WORKLIST");
		existing.setId(5L);

		when(dicomNodeConfigRepo.findById(5L)).thenReturn(Optional.of(existing));
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		var updated = dicomNodeConfigService.updateNode(5L, "New", "NEW", "new.host", 200);

		assertEquals("New", updated.getName());
		assertEquals("NEW", updated.getAet());
		assertEquals("new.host", updated.getHostname());
		assertEquals(200, updated.getPort());
		// node type is preserved
		assertEquals("WORKLIST", updated.getNodeType());
	}

	@Test
	void update_node_throws_when_id_is_unknown() {
		when(dicomNodeConfigRepo.findById(99L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> dicomNodeConfigService.updateNode(99L, "New", "NEW", "new.host", 200));
	}

	@Test
	void delete_node_delegates_to_repository() {
		dicomNodeConfigService.deleteNode(3L);

		verify(dicomNodeConfigRepo).deleteById(3L);
	}

	@Test
	void exports_nodes_to_csv_with_header_and_rows() {
		var workstation = new DicomNodeConfigEntity("Karnak", "KARNAK-GATEWAY", "localhost", 11112, "WORKSTATION");

		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of(workstation));
		when(dicomNodeConfigRepo.findByNodeType("PACS_WEB")).thenReturn(List.of());

		var csv = new String(dicomNodeConfigService.exportNodesToCsv(List.of("WORKSTATION", "PACS_WEB")),
				StandardCharsets.UTF_8);

		assertTrue(csv.contains("\"description\",\"aetitle\",\"hostname\",\"port\",\"nodeType\""));
		assertTrue(csv.contains("\"Karnak\",\"KARNAK-GATEWAY\",\"localhost\",\"11112\",\"WORKSTATION\""));
	}

	@Test
	void imports_nodes_with_node_type_column_and_persists_them() {
		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of());
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		var csv = """
				description,aetitle,hostname,port,nodeType
				Karnak,KARNAK-GATEWAY,localhost,11112,WORKSTATION
				""";

		int imported = dicomNodeConfigService
			.importNodesFromCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "WORKSTATION", ',');

		assertEquals(1, imported);
		var captor = ArgumentCaptor.forClass(DicomNodeConfigEntity.class);
		verify(dicomNodeConfigRepo).save(captor.capture());
		var saved = captor.getValue();
		assertEquals("KARNAK-GATEWAY", saved.getAeTitle());
		assertEquals(11112, saved.getPort());
		assertEquals("WORKSTATION", saved.getNodeType());
	}

	@Test
	void imports_legacy_four_column_files_using_default_node_type() {
		when(dicomNodeConfigRepo.findByNodeType("WORKLIST")).thenReturn(List.of());
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// previous-version format: name,aet,hostname,port (with a comment line)
		var csv = """
				# legacy worklist export
				Public dicomserver,ADVT,dicomserver.co.uk,104
				""";

		int imported = dicomNodeConfigService
			.importNodesFromCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "WORKLIST", ',');

		assertEquals(1, imported);
		var captor = ArgumentCaptor.forClass(DicomNodeConfigEntity.class);
		verify(dicomNodeConfigRepo).save(captor.capture());
		assertEquals("WORKLIST", captor.getValue().getNodeType());
		assertEquals("ADVT", captor.getValue().getAeTitle());
	}

	@Test
	void import_skips_duplicates_already_present_in_database() {
		var existing = new DicomNodeConfigEntity("Karnak", "KARNAK-GATEWAY", "localhost", 11112, "WORKSTATION");
		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of(existing));

		var csv = """
				description,aetitle,hostname,port,nodeType
				Karnak,KARNAK-GATEWAY,localhost,11112,WORKSTATION
				""";

		int imported = dicomNodeConfigService
			.importNodesFromCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "WORKSTATION", ',');

		assertEquals(0, imported);
		verify(dicomNodeConfigRepo, never()).save(any(DicomNodeConfigEntity.class));
	}

	@Test
	void export_dicom_nodes_includes_every_non_worklist_node() {
		var workstation = new DicomNodeConfigEntity("Karnak", "KARNAK-GATEWAY", "localhost", 11112, "WORKSTATION");
		var pacsNode = new DicomNodeConfigEntity("Public", "DICOMSERVER", "dicomserver.co.uk", 11112, "PACS_WEB");
		when(dicomNodeConfigRepo.findByNodeTypeNot("WORKLIST")).thenReturn(List.of(workstation, pacsNode));

		var csv = new String(dicomNodeConfigService.exportDicomNodes(), StandardCharsets.UTF_8);

		assertTrue(csv.contains("\"KARNAK-GATEWAY\",\"localhost\",\"11112\",\"WORKSTATION\""));
		assertTrue(csv.contains("\"DICOMSERVER\",\"dicomserver.co.uk\",\"11112\",\"PACS_WEB\""));
	}

	@Test
	void export_worklist_nodes_includes_every_worklist_node() {
		var worklist = new DicomNodeConfigEntity("Public", "ADVT", "dicomserver.co.uk", 104, "WORKLIST");
		when(dicomNodeConfigRepo.findByNodeType("WORKLIST")).thenReturn(List.of(worklist));

		var csv = new String(dicomNodeConfigService.exportWorkListNodes(), StandardCharsets.UTF_8);

		assertTrue(csv.contains("\"ADVT\",\"dicomserver.co.uk\",\"104\",\"WORKLIST\""));
	}

	@Test
	void import_dicom_nodes_dispatches_each_row_to_its_group() {
		when(dicomNodeConfigRepo.findByNodeType("WORKSTATION")).thenReturn(List.of());
		when(dicomNodeConfigRepo.findByNodeType("PACS_WEB")).thenReturn(List.of());
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// one legacy row (no type -> default WORKSTATION) and one row carrying its own
		// group
		var csv = """
				Legacy,LEG,leg.host,104
				Public,DICOMSERVER,dicomserver.co.uk,11112,PACS_WEB
				""";

		int imported = dicomNodeConfigService
			.importDicomNodes(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), ',');

		assertEquals(2, imported);
		var captor = ArgumentCaptor.forClass(DicomNodeConfigEntity.class);
		verify(dicomNodeConfigRepo, times(2)).save(captor.capture());
		var types = captor.getAllValues().stream().map(DicomNodeConfigEntity::getNodeType).toList();
		assertTrue(types.contains("WORKSTATION"));
		assertTrue(types.contains("PACS_WEB"));
	}

	@Test
	void import_worklist_nodes_forces_every_row_to_the_worklist_group() {
		when(dicomNodeConfigRepo.findByNodeType("WORKLIST")).thenReturn(List.of());
		when(dicomNodeConfigRepo.save(any(DicomNodeConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// the row carries a WORKSTATION type but must still land in the worklist group
		var csv = """
				description,aetitle,hostname,port,nodeType
				Mislabelled,ADVT,dicomserver.co.uk,104,WORKSTATION
				""";

		int imported = dicomNodeConfigService
			.importWorkListNodes(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), ',');

		assertEquals(1, imported);
		var captor = ArgumentCaptor.forClass(DicomNodeConfigEntity.class);
		verify(dicomNodeConfigRepo).save(captor.capture());
		assertEquals("WORKLIST", captor.getValue().getNodeType());
	}

	@Test
	void get_groups_returns_group_names_in_order() {
		when(dicomNodeGroupRepo.findAllByOrderByNameAsc())
			.thenReturn(List.of(new DicomNodeGroupEntity("PACS_WEB"), new DicomNodeGroupEntity("WORKSTATION")));

		assertEquals(List.of("PACS_WEB", "WORKSTATION"), dicomNodeConfigService.getGroups());
	}

	@Test
	void create_group_persists_a_new_group() {
		when(dicomNodeGroupRepo.existsByName("Workstations")).thenReturn(false);

		dicomNodeConfigService.createGroup("Workstations");

		var captor = ArgumentCaptor.forClass(DicomNodeGroupEntity.class);
		verify(dicomNodeGroupRepo).save(captor.capture());
		assertEquals("Workstations", captor.getValue().getName());
	}

	@Test
	void create_group_rejects_a_duplicate_name() {
		when(dicomNodeGroupRepo.existsByName("PACS_WEB")).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> dicomNodeConfigService.createGroup("PACS_WEB"));
		verify(dicomNodeGroupRepo, never()).save(any(DicomNodeGroupEntity.class));
	}

	@Test
	void create_group_rejects_the_reserved_worklist_name() {
		assertThrows(IllegalArgumentException.class, () -> dicomNodeConfigService.createGroup("worklist"));
		verify(dicomNodeGroupRepo, never()).save(any(DicomNodeGroupEntity.class));
	}

	@Test
	void rename_group_moves_its_nodes_and_updates_the_group() {
		var group = new DicomNodeGroupEntity("OLD");
		var node = new DicomNodeConfigEntity("n", "AET", "h", 11112, "OLD");
		when(dicomNodeGroupRepo.findByName("OLD")).thenReturn(Optional.of(group));
		when(dicomNodeGroupRepo.existsByName("NEW")).thenReturn(false);
		when(dicomNodeConfigRepo.findByNodeType("OLD")).thenReturn(List.of(node));

		dicomNodeConfigService.renameGroup("OLD", "NEW");

		assertEquals("NEW", node.getNodeType());
		assertEquals("NEW", group.getName());
		verify(dicomNodeConfigRepo).save(node);
		verify(dicomNodeGroupRepo).save(group);
	}

	@Test
	void delete_group_removes_the_group_and_its_nodes() {
		var group = new DicomNodeGroupEntity("PACS_WEB");
		var node = new DicomNodeConfigEntity("n", "AET", "h", 11112, "PACS_WEB");
		when(dicomNodeGroupRepo.findByName("PACS_WEB")).thenReturn(Optional.of(group));
		when(dicomNodeConfigRepo.findByNodeType("PACS_WEB")).thenReturn(List.of(node));

		int removed = dicomNodeConfigService.deleteGroup("PACS_WEB");

		assertEquals(1, removed);
		verify(dicomNodeConfigRepo).deleteAll(List.of(node));
		verify(dicomNodeGroupRepo).delete(group);
	}

	@Test
	void delete_group_rejects_the_reserved_worklist_group() {
		assertThrows(IllegalArgumentException.class, () -> dicomNodeConfigService.deleteGroup("WORKLIST"));
	}

}
