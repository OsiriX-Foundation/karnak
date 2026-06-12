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
import lombok.Getter;
import lombok.Setter;

public class ProfilePipeBody {

	@Setter
	@Getter
	private String name;

	@Setter
	@Getter
	private String version;

	@Setter
	@Getter
	private String minimumKarnakVersion;

	@Setter
	@Getter
	private String defaultIssuerOfPatientID;

	private List<ProfileElementBody> profiles;

	@Setter
	@Getter
	private List<MaskBody> masks;

	public List<ProfileElementBody> getProfileElements() {
		return profiles;
	}

	public void setProfileElements(List<ProfileElementBody> profiles) {
		this.profiles = profiles;
	}

}
