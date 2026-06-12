/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profilebody;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class ProfileElementBody {

	@Setter
	@Getter
	private String name;

	@Setter
	@Getter
	private String codename;

	@Setter
	@Getter
	private String condition;

	@Getter
	@Setter
	private String action;

	@Getter
	@Setter
	private String option;

	@Getter
	@Setter
	private String args;

	private List<String> tagEntities;

	@Getter
	@Setter
	private List<String> excludedTags;

	@Getter
	@Setter
	private Map<String, String> arguments;

	public List<String> getTags() {
		return tagEntities;
	}

	public void setTags(List<String> tagEntities) {
		this.tagEntities = tagEntities;
	}

}
