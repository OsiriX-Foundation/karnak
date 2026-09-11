/*
 * Copyright (c) 2024-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.authconfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karnak.backend.enums.AuthConfigType;
import org.springframework.validation.annotation.Validated;

/**
 * API model exposed by the REST layer for an authentication configuration (OAuth2
 * client used by a STOW-RS destination). Decouples the wire format from
 * {@link org.karnak.backend.data.entity.AuthConfigEntity}. The technical database id is
 * not exposed: the public {@code code}, conveyed by the URL path, identifies the
 * resource. {@code clientSecret} is write-only: it is accepted on create/update but
 * never populated when the entity is mapped back for a GET response.
 */
@Getter
@Setter
@Validated
@NoArgsConstructor
public class AuthConfigModel implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Code is mandatory")
	private String code;

	private String clientId;

	// Write-only: never populated by AuthConfigMapper#toModel.
	private String clientSecret;

	private String accessTokenUrl;

	private String scope;

	@NotNull(message = "Authentication configuration type is mandatory")
	private AuthConfigType authConfigType;

}
