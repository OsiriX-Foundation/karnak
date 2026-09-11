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

import org.karnak.backend.data.entity.AuthConfigEntity;
import org.karnak.backend.enums.AuthConfigType;

/**
 * Maps between {@link AuthConfigEntity} and {@link AuthConfigModel}.
 */
public final class AuthConfigMapper {

	private AuthConfigMapper() {
	}

	/**
	 * Maps an entity to its wire model. {@code clientSecret} is deliberately left unset:
	 * it is a write-only field and must never be echoed back to a client.
	 */
	public static AuthConfigModel toModel(AuthConfigEntity entity) {
		if (entity == null) {
			return null;
		}
		AuthConfigModel model = new AuthConfigModel();
		model.setCode(entity.getCode());
		model.setClientId(entity.getClientId());
		model.setAccessTokenUrl(entity.getAccessTokenUrl());
		model.setScope(entity.getScope());
		model.setAuthConfigType(entity.getAuthConfigType());
		return model;
	}

	/** Builds a new entity from a create payload. The id is left null. */
	public static AuthConfigEntity toEntity(AuthConfigModel model) {
		if (model == null) {
			return null;
		}
		AuthConfigEntity entity = new AuthConfigEntity();
		entity.setCode(model.getCode());
		entity.setClientId(model.getClientId());
		entity.setClientSecret(model.getClientSecret());
		entity.setAccessTokenUrl(model.getAccessTokenUrl());
		entity.setScope(model.getScope());
		entity.setAuthConfigType(model.getAuthConfigType() != null ? model.getAuthConfigType() : AuthConfigType.OAUTH2);
		return entity;
	}

	/**
	 * Merges an update payload onto an existing entity in place. {@code code} is the
	 * resource identifier (conveyed by the URL path) and is never overwritten here. A
	 * blank/null {@code clientSecret} in the payload leaves the stored secret untouched,
	 * so a client can update the other fields without having to resend it.
	 */
	public static void mergeInto(AuthConfigEntity existing, AuthConfigModel model) {
		if (existing == null || model == null) {
			return;
		}
		if (model.getClientId() != null) {
			existing.setClientId(model.getClientId());
		}
		if (model.getClientSecret() != null && !model.getClientSecret().isBlank()) {
			existing.setClientSecret(model.getClientSecret());
		}
		if (model.getAccessTokenUrl() != null) {
			existing.setAccessTokenUrl(model.getAccessTokenUrl());
		}
		if (model.getScope() != null) {
			existing.setScope(model.getScope());
		}
		if (model.getAuthConfigType() != null) {
			existing.setAuthConfigType(model.getAuthConfigType());
		}
	}

}
