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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.AuthConfigEntity;
import org.karnak.backend.enums.AuthConfigType;
import org.karnak.backend.model.authconfig.AuthConfigModel;
import org.karnak.backend.service.AuthConfigService;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test for {@link AuthConfigController}. Calls the controller methods directly
 * against a mocked {@link AuthConfigService}, mirroring how {@code ProjectServiceTest}
 * exercises the service layer of the sibling {@code ProjectController}.
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthConfigControllerTest {

	private final AuthConfigService authConfigServiceMock = Mockito.mock(AuthConfigService.class);

	private AuthConfigController authConfigController;

	@BeforeEach
	void setUp() {
		authConfigController = new AuthConfigController(authConfigServiceMock);
	}

	private static AuthConfigEntity entity(String code) {
		AuthConfigEntity entity = new AuthConfigEntity();
		entity.setId(1L);
		entity.setCode(code);
		entity.setClientId("clientId");
		entity.setClientSecret("s3cr3t");
		entity.setAccessTokenUrl("https://idp.example.org/token");
		entity.setScope("openid");
		entity.setAuthConfigType(AuthConfigType.OAUTH2);
		return entity;
	}

	private static AuthConfigModel model(String code) {
		AuthConfigModel model = new AuthConfigModel();
		model.setCode(code);
		model.setClientId("clientId");
		model.setClientSecret("s3cr3t");
		model.setAccessTokenUrl("https://idp.example.org/token");
		model.setScope("openid");
		model.setAuthConfigType(AuthConfigType.OAUTH2);
		return model;
	}

	@Test
	void retrieveAllAuthConfigs_returns_no_content_when_empty() {
		Mockito.when(authConfigServiceMock.retrieveAllAuthConfigs()).thenReturn(Collections.emptyList());

		ResponseEntity<List<AuthConfigModel>> response = authConfigController.retrieveAllAuthConfigs();

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void retrieveAllAuthConfigs_never_exposes_the_client_secret() {
		Mockito.when(authConfigServiceMock.retrieveAllAuthConfigs()).thenReturn(List.of(entity("shanoir-stow")));

		ResponseEntity<List<AuthConfigModel>> response = authConfigController.retrieveAllAuthConfigs();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().size());
		assertEquals("shanoir-stow", response.getBody().get(0).getCode());
		assertNull(response.getBody().get(0).getClientSecret());
	}

	@Test
	void createAuthConfig_persists_and_returns_201() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("shanoir-stow")).thenReturn(null);
		Mockito.when(authConfigServiceMock.save(Mockito.any(AuthConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		ResponseEntity<AuthConfigModel> response = authConfigController.createAuthConfig(model("shanoir-stow"));

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("shanoir-stow", response.getBody().getCode());
		assertNull(response.getBody().getClientSecret());
		Mockito.verify(authConfigServiceMock).save(Mockito.any(AuthConfigEntity.class));
	}

	@Test
	void createAuthConfig_conflicts_when_code_already_exists() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("shanoir-stow")).thenReturn(entity("shanoir-stow"));

		ResponseEntity<AuthConfigModel> response = authConfigController.createAuthConfig(model("shanoir-stow"));

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		Mockito.verify(authConfigServiceMock, Mockito.never()).save(Mockito.any());
	}

	@Test
	void retrieveAuthConfig_found() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("shanoir-stow")).thenReturn(entity("shanoir-stow"));

		ResponseEntity<AuthConfigModel> response = authConfigController.retrieveAuthConfig("shanoir-stow");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("clientId", response.getBody().getClientId());
		assertNull(response.getBody().getClientSecret());
	}

	@Test
	void retrieveAuthConfig_not_found() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("missing")).thenReturn(null);

		ResponseEntity<AuthConfigModel> response = authConfigController.retrieveAuthConfig("missing");

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void updateAuthConfig_not_found() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("missing")).thenReturn(null);

		ResponseEntity<AuthConfigModel> response = authConfigController.updateAuthConfig("missing", model("missing"));

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void updateAuthConfig_merges_fields_and_leaves_a_blank_secret_untouched() {
		AuthConfigEntity existing = entity("shanoir-stow");
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("shanoir-stow")).thenReturn(existing);
		Mockito.when(authConfigServiceMock.update(Mockito.any(AuthConfigEntity.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		AuthConfigModel payload = new AuthConfigModel();
		payload.setScope("openid profile");
		payload.setClientSecret("");

		ResponseEntity<AuthConfigModel> response = authConfigController.updateAuthConfig("shanoir-stow", payload);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("openid profile", existing.getScope());
		// clientSecret was blank in the payload: the stored one must be unchanged.
		assertEquals("s3cr3t", existing.getClientSecret());
	}

	@Test
	void deleteAuthConfig_removes_and_returns_204() {
		AuthConfigEntity existing = entity("shanoir-stow");
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("shanoir-stow")).thenReturn(existing);

		ResponseEntity<Void> response = authConfigController.deleteAuthConfig("shanoir-stow");

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		Mockito.verify(authConfigServiceMock).remove(existing);
	}

	@Test
	void deleteAuthConfig_not_found() {
		Mockito.when(authConfigServiceMock.retrieveAuthConfigByCode("missing")).thenReturn(null);

		ResponseEntity<Void> response = authConfigController.deleteAuthConfig("missing");

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Mockito.verify(authConfigServiceMock, Mockito.never()).remove(Mockito.any());
	}

}
