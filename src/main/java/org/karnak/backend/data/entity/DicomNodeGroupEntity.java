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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NullUnmarked;

import java.io.Serializable;
import java.util.Objects;

/**
 * A user-defined group of DICOM nodes (formerly the fixed "node type"). The group name
 * matches the {@code node_type} column of {@link DicomNodeConfigEntity}. The reserved
 * {@code WORKLIST} group is intentionally never stored here: worklists form a single,
 * non-modifiable group.
 */
@Entity(name = "DicomNodeGroup")
@Table(name = "dicom_node_group")
@NullUnmarked
public class DicomNodeGroupEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	public DicomNodeGroupEntity() {
	}

	public DicomNodeGroupEntity(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotBlank(message = "Group name is mandatory")
	@Size(max = 50, message = "Group name has more than 50 characters")
	@Column(name = "name", unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DicomNodeGroupEntity that = (DicomNodeGroupEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "DicomNodeGroup [id=" + id + ", name=" + name + "]";
	}

}
