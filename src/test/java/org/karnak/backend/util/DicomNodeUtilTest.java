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

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.karnak.backend.model.dicom.DicomNodeList;

/**
 * {@link DicomNodeUtil} loads the node lists bundled as CSV resources under
 * {@code /config}.
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class DicomNodeUtilTest {

	@Test
	void loads_the_locally_defined_dicom_node_types() {
		List<DicomNodeList> nodeTypes = DicomNodeUtil.getAllDicomNodeTypesDefinedLocally();

		assertNotNull(nodeTypes);
		// Workstations + PACS Public WEB.
		assertEquals(2, nodeTypes.size());
		nodeTypes.forEach(org.junit.jupiter.api.Assertions::assertNotNull);
	}

	@Test
	void loads_the_locally_defined_worklist_nodes() {
		assertNotNull(DicomNodeUtil.getAllWorkListNodesDefinedLocally());
	}

}