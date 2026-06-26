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

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.karnak.backend.data.entity.DicomNodeConfigEntity;
import org.karnak.backend.data.entity.DicomNodeGroupEntity;
import org.karnak.backend.data.repo.DicomNodeConfigRepo;
import org.karnak.backend.data.repo.DicomNodeGroupRepo;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weasis.dicom.param.DicomNode;
import org.jspecify.annotations.NullUnmarked;

@Service
@NullUnmarked
public class DicomNodeConfigService {

	/**
	 * Default group used for quick "Save Node" actions that do not pick a group
	 * explicitly.
	 */
	public static final String NODE_TYPE_WORKSTATION = "WORKSTATION";

	/**
	 * Reserved group of worklist nodes: a single group that cannot be
	 * created/edited/deleted.
	 */
	public static final String NODE_TYPE_WORKLIST = "WORKLIST";

	/** Display name of the reserved worklist group. */
	private static final String WORKLIST_DISPLAY_NAME = "Worklists";

	/** Header row written on export and skipped on import. */
	static final String[] CSV_HEADER = { "description", "aetitle", "hostname", "port", "nodeType" };

	private final DicomNodeConfigRepo dicomNodeConfigRepo;

	private final DicomNodeGroupRepo dicomNodeGroupRepo;

	@Autowired
	public DicomNodeConfigService(DicomNodeConfigRepo dicomNodeConfigRepo, DicomNodeGroupRepo dicomNodeGroupRepo) {
		this.dicomNodeConfigRepo = dicomNodeConfigRepo;
		this.dicomNodeGroupRepo = dicomNodeGroupRepo;
	}

	/**
	 * @return one list per user-defined DICOM node group (the reserved worklist group is
	 * excluded), ordered by group name; groups with no node yet are returned empty
	 */
	public List<DicomNodeList> getAllDicomNodeTypes() {
		return dicomNodeGroupRepo.findAllByOrderByNameAsc()
			.stream()
			.map(group -> getNodeListByType(group.getName(), group.getName()))
			.collect(Collectors.toList());
	}

	public DicomNodeList getWorkListNodes() {
		return getNodeListByType(NODE_TYPE_WORKLIST, WORKLIST_DISPLAY_NAME);
	}

	/**
	 * @return the names of the user-defined DICOM node groups, ordered alphabetically
	 * (the reserved worklist group is not included)
	 */
	public List<String> getGroups() {
		return dicomNodeGroupRepo.findAllByOrderByNameAsc()
			.stream()
			.map(DicomNodeGroupEntity::getName)
			.collect(Collectors.toList());
	}

	/** Create a new (empty) DICOM node group. */
	public void createGroup(String name) {
		String groupName = validateGroupName(name);
		if (dicomNodeGroupRepo.existsByName(groupName)) {
			throw new IllegalArgumentException("A group named \"" + groupName + "\" already exists");
		}
		dicomNodeGroupRepo.save(new DicomNodeGroupEntity(groupName));
	}

	/** Rename a DICOM node group, moving all of its nodes to the new name. */
	public void renameGroup(String oldName, String newName) {
		String target = validateGroupName(newName);
		var group = dicomNodeGroupRepo.findByName(oldName)
			.orElseThrow(() -> new IllegalArgumentException("No group named \"" + oldName + "\""));
		if (!target.equals(oldName) && dicomNodeGroupRepo.existsByName(target)) {
			throw new IllegalArgumentException("A group named \"" + target + "\" already exists");
		}
		for (DicomNodeConfigEntity entity : dicomNodeConfigRepo.findByNodeType(oldName)) {
			entity.setNodeType(target);
			dicomNodeConfigRepo.save(entity);
		}
		group.setName(target);
		dicomNodeGroupRepo.save(group);
	}

	/**
	 * Delete a DICOM node group together with all of its nodes.
	 * @return the number of nodes that were removed
	 */
	public int deleteGroup(String name) {
		if (NODE_TYPE_WORKLIST.equalsIgnoreCase(name)) {
			throw new IllegalArgumentException("The worklist group cannot be deleted");
		}
		var group = dicomNodeGroupRepo.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException("No group named \"" + name + "\""));
		var nodes = dicomNodeConfigRepo.findByNodeType(name);
		dicomNodeConfigRepo.deleteAll(nodes);
		dicomNodeGroupRepo.delete(group);
		return nodes.size();
	}

	/**
	 * Persist a new DICOM node configuration.
	 * @return the saved node, with its generated id and type set
	 */
	public ConfigNode saveNode(String description, String aeTitle, String hostname, Integer port, String nodeType) {
		ensureGroup(nodeType);
		var entity = new DicomNodeConfigEntity(normalizeDescription(description, aeTitle), aeTitle, hostname, port,
				nodeType);
		return toConfigNode(dicomNodeConfigRepo.save(entity));
	}

	/**
	 * Make sure a group exists for the given node type, creating it on demand. The
	 * reserved worklist group is never materialised as a manageable group.
	 */
	private void ensureGroup(String nodeType) {
		if (nodeType == null) {
			return;
		}
		String name = nodeType.trim();
		if (name.isEmpty() || NODE_TYPE_WORKLIST.equalsIgnoreCase(name) || dicomNodeGroupRepo.existsByName(name)) {
			return;
		}
		dicomNodeGroupRepo.save(new DicomNodeGroupEntity(name));
	}

	/**
	 * Update an existing DICOM node configuration. The node type is left unchanged.
	 * @return the updated node
	 */
	public ConfigNode updateNode(Long id, String description, String aeTitle, String hostname, Integer port) {
		var entity = dicomNodeConfigRepo.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("No DICOM node configuration with id " + id));
		entity.setDescription(normalizeDescription(description, aeTitle));
		entity.setAeTitle(aeTitle);
		entity.setHostname(hostname);
		entity.setPort(port);
		return toConfigNode(dicomNodeConfigRepo.save(entity));
	}

	public void deleteNode(Long id) {
		dicomNodeConfigRepo.deleteById(id);
	}

	/**
	 * Export every DICOM node from every group (the reserved worklist group is excluded)
	 * as a CSV document.
	 * @return the CSV content encoded as UTF-8 bytes
	 */
	public byte[] exportDicomNodes() {
		return toCsv(dicomNodeConfigRepo.findByNodeTypeNot(NODE_TYPE_WORKLIST));
	}

	/**
	 * Export every worklist node as a CSV document.
	 * @return the CSV content encoded as UTF-8 bytes
	 */
	public byte[] exportWorkListNodes() {
		return toCsv(dicomNodeConfigRepo.findByNodeType(NODE_TYPE_WORKLIST));
	}

	/**
	 * Export the node configurations of the given types as a CSV document.
	 * @param nodeTypes the node types to include in the export
	 * @return the CSV content encoded as UTF-8 bytes
	 */
	public byte[] exportNodesToCsv(List<String> nodeTypes) {
		var entities = new ArrayList<DicomNodeConfigEntity>();
		for (String nodeType : nodeTypes) {
			entities.addAll(dicomNodeConfigRepo.findByNodeType(nodeType));
		}
		return toCsv(entities);
	}

	private static byte[] toCsv(List<DicomNodeConfigEntity> entities) {
		var stringWriter = new StringWriter();
		try (var csvWriter = new CSVWriter(stringWriter)) {
			csvWriter.writeNext(CSV_HEADER);
			for (DicomNodeConfigEntity entity : entities) {
				csvWriter.writeNext(new String[] { emptyIfNull(entity.getDescription()),
						emptyIfNull(entity.getAeTitle()), emptyIfNull(entity.getHostname()),
						entity.getPort() == null ? "" : entity.getPort().toString(),
						emptyIfNull(entity.getNodeType()) });
			}
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot export DICOM nodes to CSV", e);
		}
		return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Import DICOM nodes from a CSV document, spreading the rows over their groups: a
	 * row's fifth {@code nodeType} column selects its group (created on demand), and rows
	 * without one fall back to the default workstation group (this covers the flat node
	 * files of previous versions).
	 * @return the number of nodes actually persisted
	 */
	public int importDicomNodes(InputStream inputStream, char separator) {
		return importNodesFromCsv(inputStream, NODE_TYPE_WORKSTATION, separator, false);
	}

	/**
	 * Import worklist nodes from a CSV document: every row is stored as a worklist node,
	 * regardless of any {@code nodeType} column it may carry.
	 * @return the number of nodes actually persisted
	 */
	public int importWorkListNodes(InputStream inputStream, char separator) {
		return importNodesFromCsv(inputStream, NODE_TYPE_WORKLIST, separator, true);
	}

	/**
	 * Import node configurations from a CSV document. Each row is expected to contain at
	 * least {@code description,aetitle,hostname,port}; a fifth {@code nodeType} column is
	 * used when present, otherwise {@code defaultNodeType} is applied (this allows
	 * migrating the flat node files of previous versions). A header row, blank lines and
	 * lines starting with {@code #} are skipped, as are rows whose
	 * {@code aetitle/hostname/port} already exist for that type (so re-importing is
	 * safe).
	 * @return the number of nodes actually persisted
	 */
	public int importNodesFromCsv(InputStream inputStream, String defaultNodeType, char separator) {
		return importNodesFromCsv(inputStream, defaultNodeType, separator, false);
	}

	/**
	 * @param nodeType the group applied to rows without a {@code nodeType} column, or to
	 * every row when {@code forceNodeType} is {@code true}
	 * @param forceNodeType when {@code true}, every row is stored under {@code nodeType}
	 * even if it carries a different {@code nodeType} column
	 * @return the number of nodes actually persisted
	 */
	public int importNodesFromCsv(InputStream inputStream, String nodeType, char separator, boolean forceNodeType) {
		var parser = new CSVParserBuilder().withSeparator(separator).build();
		Map<String, Set<String>> keysByType = new HashMap<>();
		int imported = 0;
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			.withCSVParser(parser)
			.build()) {
			String[] row;
			while ((row = reader.readNext()) != null) {
				if (row.length < 4) {
					continue;
				}
				String description = trim(row[0]);
				if (description.startsWith("#") || description.equalsIgnoreCase("description")
						|| description.equalsIgnoreCase("name")) {
					continue;
				}
				String aeTitle = trim(row[1]);
				String hostname = trim(row[2]);
				Integer port = parsePort(row[3]);
				if (aeTitle.isEmpty() || hostname.isEmpty() || port == null) {
					continue;
				}
				String rowType = (!forceNodeType && row.length >= 5 && !trim(row[4]).isEmpty()) ? trim(row[4])
						: nodeType;

				Set<String> keys = keysByType.computeIfAbsent(rowType, this::loadExistingKeys);
				if (!keys.add(nodeKey(aeTitle, hostname, port))) {
					continue;
				}
				saveNode(description, aeTitle, hostname, port, rowType);
				imported++;
			}
		}
		catch (IOException | CsvValidationException e) {
			throw new IllegalStateException("Cannot import DICOM nodes from CSV: " + e.getMessage(), e);
		}
		return imported;
	}

	private Set<String> loadExistingKeys(String nodeType) {
		Set<String> keys = new HashSet<>();
		for (DicomNodeConfigEntity entity : dicomNodeConfigRepo.findByNodeType(nodeType)) {
			keys.add(nodeKey(entity.getAeTitle(), entity.getHostname(), entity.getPort()));
		}
		return keys;
	}

	private static String nodeKey(String aeTitle, String hostname, Integer port) {
		return aeTitle + '\\' + hostname + '\\' + port;
	}

	private static Integer parsePort(String value) {
		try {
			return Integer.valueOf(value.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}

	private static String emptyIfNull(String value) {
		return value == null ? "" : value;
	}

	private DicomNodeList getNodeListByType(String nodeType, String displayName) {
		var entities = dicomNodeConfigRepo.findByNodeType(nodeType);
		var nodeList = new DicomNodeList(displayName);
		for (DicomNodeConfigEntity entity : entities) {
			nodeList.add(toConfigNode(entity));
		}
		return nodeList;
	}

	private String validateGroupName(String name) {
		String groupName = name == null ? "" : name.trim();
		if (groupName.isEmpty()) {
			throw new IllegalArgumentException("Group name is required");
		}
		if (NODE_TYPE_WORKLIST.equalsIgnoreCase(groupName)) {
			throw new IllegalArgumentException("\"" + NODE_TYPE_WORKLIST + "\" is a reserved group name");
		}
		return groupName;
	}

	private static ConfigNode toConfigNode(DicomNodeConfigEntity entity) {
		var dicomNode = new DicomNode(entity.getAeTitle(), entity.getHostname(), entity.getPort());
		var node = new ConfigNode(normalizeDescription(entity.getDescription(), entity.getAeTitle()), dicomNode);
		node.setId(entity.getId());
		node.setNodeType(entity.getNodeType());
		return node;
	}

	private static String normalizeDescription(String description, String aeTitle) {
		return (description == null || description.isBlank()) ? aeTitle : description;
	}

}
