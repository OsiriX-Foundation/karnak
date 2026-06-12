/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import java.io.InputStream;
import java.util.List;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.backend.service.DicomNodeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DicomNodeUtil {

	private final DicomNodeConfigService dicomNodeConfigService;

	@Autowired
	public DicomNodeUtil(DicomNodeConfigService dicomNodeConfigService) {
		this.dicomNodeConfigService = dicomNodeConfigService;
	}

	public List<DicomNodeList> getAllDicomNodeTypes() {
		return dicomNodeConfigService.getAllDicomNodeTypes();
	}

	public DicomNodeList getWorkListNodes() {
		return dicomNodeConfigService.getWorkListNodes();
	}

	public ConfigNode saveDicomNode(String description, String aeTitle, String hostname, Integer port,
			String nodeType) {
		return dicomNodeConfigService.saveNode(description, aeTitle, hostname, port, nodeType);
	}

	public ConfigNode updateDicomNode(Long id, String description, String aeTitle, String hostname, Integer port) {
		return dicomNodeConfigService.updateNode(id, description, aeTitle, hostname, port);
	}

	public void deleteDicomNode(Long id) {
		dicomNodeConfigService.deleteNode(id);
	}

	/** Export every DICOM node from every group (worklists excluded) as CSV. */
	public byte[] exportDicomNodes() {
		return dicomNodeConfigService.exportDicomNodes();
	}

	/** Export every worklist node as CSV. */
	public byte[] exportWorkListNodes() {
		return dicomNodeConfigService.exportWorkListNodes();
	}

	/** Import DICOM nodes from CSV, dispatching each row to its group. */
	public int importDicomNodes(InputStream inputStream, char separator) {
		return dicomNodeConfigService.importDicomNodes(inputStream, separator);
	}

	/** Import worklist nodes from CSV, storing every row as a worklist node. */
	public int importWorkListNodes(InputStream inputStream, char separator) {
		return dicomNodeConfigService.importWorkListNodes(inputStream, separator);
	}

	public List<String> getGroups() {
		return dicomNodeConfigService.getGroups();
	}

	public void createGroup(String name) {
		dicomNodeConfigService.createGroup(name);
	}

	public void renameGroup(String oldName, String newName) {
		dicomNodeConfigService.renameGroup(oldName, newName);
	}

	public int deleteGroup(String name) {
		return dicomNodeConfigService.deleteGroup(name);
	}

}
