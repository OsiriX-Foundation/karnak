/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "Arguments")
@Table(name = "arguments")
public class ArgumentEntity implements Serializable {

	private static final long serialVersionUID = -839421871919135822L;

	private Long id;

	private ProfileElementEntity profileElementEntity;

	private String key;

	private String value;

	public ArgumentEntity() {
	}

	public ArgumentEntity(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public ArgumentEntity(String key, String value, ProfileElementEntity profileElementEntity) {
		this.key = key;
		this.value = value;
		this.profileElementEntity = profileElementEntity;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne()
	@JoinColumn(name = "profile_element_id", nullable = false)
	public ProfileElementEntity getProfileElementEntity() {
		return profileElementEntity;
	}

	public void setProfileElementEntity(ProfileElementEntity profileElementEntity) {
		this.profileElementEntity = profileElementEntity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ArgumentEntity that = (ArgumentEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(key, that.key) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, key, value);
	}

}
