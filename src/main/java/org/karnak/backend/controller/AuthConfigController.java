/*
 * Copyright (c) 2024-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.karnak.backend.constant.ApiVersion;
import org.karnak.backend.constant.EndPoint;
import org.karnak.backend.data.entity.AuthConfigEntity;
import org.karnak.backend.model.authconfig.AuthConfigMapper;
import org.karnak.backend.model.authconfig.AuthConfigModel;
import org.karnak.backend.service.AuthConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller managing authentication configurations (OAuth2 clients used by STOW-RS
 * destinations). Only {@link AuthConfigModel} (never the JPA entity) is exposed at the
 * REST boundary. Resources are keyed on {@code code} (a String), not a uuid:
 * {@link AuthConfigEntity} has no uuid column.
 */
@RestController
@RequestMapping(EndPoint.AUTH_CONFIGS_PATH)
@Tag(name = "AuthConfig", description = "API Endpoints for Authentication Configurations (OAuth2)")
public class AuthConfigController {

	private final AuthConfigService authConfigService;

	@Autowired
	public AuthConfigController(final AuthConfigService authConfigService) {
		this.authConfigService = authConfigService;
	}

	@Operation(summary = "List all authentication configurations")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Authentication configurations found"),
			@ApiResponse(responseCode = "204", description = "No authentication configuration configured",
					content = @Content) })
	@GetMapping(produces = ApiVersion.V1_APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('karnak_read')")
	public ResponseEntity<List<AuthConfigModel>> retrieveAllAuthConfigs() {
		List<AuthConfigEntity> authConfigs = authConfigService.retrieveAllAuthConfigs();
		if (authConfigs.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(authConfigs.stream().map(AuthConfigMapper::toModel).toList());
	}

	@Operation(summary = "Create an authentication configuration")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Authentication configuration created"),
			@ApiResponse(responseCode = "400", description = "Missing or invalid code", content = @Content),
			@ApiResponse(responseCode = "409", description = "Code already exists", content = @Content) })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = ApiVersion.V1_APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('karnak_create')")
	public ResponseEntity<AuthConfigModel> createAuthConfig(@Valid @RequestBody AuthConfigModel authConfigModel) {
		if (authConfigService.retrieveAuthConfigByCode(authConfigModel.getCode()) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		AuthConfigEntity entity = AuthConfigMapper.toEntity(authConfigModel);
		AuthConfigEntity saved = authConfigService.save(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(AuthConfigMapper.toModel(saved));
	}

	@Operation(summary = "Get an authentication configuration by its code")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Authentication configuration found"),
			@ApiResponse(responseCode = "404", description = "Authentication configuration not found",
					content = @Content) })
	@GetMapping(value = "/{code}", produces = ApiVersion.V1_APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('karnak_read')")
	public ResponseEntity<AuthConfigModel> retrieveAuthConfig(@PathVariable("code") String code) {
		AuthConfigEntity authConfig = authConfigService.retrieveAuthConfigByCode(code);
		return authConfig == null ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(AuthConfigMapper.toModel(authConfig));
	}

	@Operation(summary = "Update an authentication configuration",
			description = "Updatable fields: clientId, clientSecret, accessTokenUrl, scope, authConfigType. "
					+ "A blank or missing clientSecret leaves the stored secret untouched.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Authentication configuration updated"),
			@ApiResponse(responseCode = "404", description = "Authentication configuration not found",
					content = @Content) })
	@PutMapping(value = "/{code}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = ApiVersion.V1_APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('karnak_update')")
	public ResponseEntity<AuthConfigModel> updateAuthConfig(@PathVariable("code") String code,
			@RequestBody AuthConfigModel authConfigModel) {
		AuthConfigEntity existing = authConfigService.retrieveAuthConfigByCode(code);
		if (existing == null) {
			return ResponseEntity.notFound().build();
		}
		AuthConfigMapper.mergeInto(existing, authConfigModel);
		AuthConfigEntity updated = authConfigService.update(existing);
		return ResponseEntity.ok(AuthConfigMapper.toModel(updated));
	}

	@Operation(summary = "Delete an authentication configuration")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Authentication configuration deleted"),
			@ApiResponse(responseCode = "404", description = "Authentication configuration not found",
					content = @Content) })
	@DeleteMapping(value = "/{code}", produces = ApiVersion.V1_APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('karnak_delete')")
	public ResponseEntity<Void> deleteAuthConfig(@PathVariable("code") String code) {
		AuthConfigEntity existing = authConfigService.retrieveAuthConfigByCode(code);
		if (existing == null) {
			return ResponseEntity.notFound().build();
		}
		authConfigService.remove(existing);
		return ResponseEntity.noContent().build();
	}

}
