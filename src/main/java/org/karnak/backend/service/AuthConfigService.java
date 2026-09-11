/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import java.util.List;
import org.jspecify.annotations.NullUnmarked;
import org.karnak.backend.data.entity.AuthConfigEntity;
import org.karnak.backend.data.repo.AuthConfigRepo;
import org.karnak.frontend.util.CollatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Authentication configuration service
 */
@Service
@NullUnmarked
public class AuthConfigService {

	private final AuthConfigRepo authConfigRepo;

	@Autowired
	public AuthConfigService(final AuthConfigRepo authConfigRepo) {
		this.authConfigRepo = authConfigRepo;
	}

	/**
	 * Retrieve the identifiers (codes) of all the stored authentication configurations,
	 * sorted alphabetically so they read consistently in a selection combo.
	 * @return the list of authentication-config identifiers
	 */
	public List<String> getAllAuthConfigCodes() {
		return authConfigRepo.findAll()
			.stream()
			.map(AuthConfigEntity::getCode)
			.sorted(CollatorUtils.stringComparator())
			.toList();
	}

	/**
	 * Retrieve all authentication configurations, sorted by code.
	 * @return authentication configurations found
	 */
	public List<AuthConfigEntity> retrieveAllAuthConfigs() {
		return authConfigRepo.findAll()
			.stream()
			.sorted(CollatorUtils.comparing(AuthConfigEntity::getCode))
			.toList();
	}

	/**
	 * Retrieve an authentication configuration by its code.
	 * @param code the identifier
	 * @return the matching entity, or {@code null} when it does not exist
	 */
	public AuthConfigEntity retrieveAuthConfigByCode(String code) {
		return code == null ? null : authConfigRepo.findByCode(code);
	}

	/**
	 * Save (create) an authentication configuration. Writes go through
	 * {@link AuthConfigRepo} only: clientId/clientSecret/accessTokenUrl/scope are
	 * pgcrypto-encrypted at the column level via {@code @ColumnTransformer}, never with
	 * raw SQL.
	 * @param authConfigEntity the entity to persist
	 * @return the persisted entity
	 */
	public AuthConfigEntity save(AuthConfigEntity authConfigEntity) {
		return authConfigRepo.saveAndFlush(authConfigEntity);
	}

	/**
	 * Update an existing authentication configuration.
	 * @param authConfigEntity the entity to persist
	 * @return the persisted entity
	 */
	public AuthConfigEntity update(AuthConfigEntity authConfigEntity) {
		return authConfigRepo.saveAndFlush(authConfigEntity);
	}

	/**
	 * Delete an authentication configuration.
	 * @param authConfigEntity the entity to remove
	 */
	public void remove(AuthConfigEntity authConfigEntity) {
		authConfigRepo.deleteById(authConfigEntity.getId());
		authConfigRepo.flush();
	}

}