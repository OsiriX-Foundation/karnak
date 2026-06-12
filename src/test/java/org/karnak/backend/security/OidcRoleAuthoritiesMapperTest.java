/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OidcRoleAuthoritiesMapperTest {

	private final OidcRoleAuthoritiesMapper mapper = new OidcRoleAuthoritiesMapper();

	@Test
	void should_map_realm_roles_of_id_token_to_karnak_roles() {
		// Init data
		OidcIdToken idToken = OidcIdToken.withTokenValue("token")
			.claim("sub", "user-id")
			.claim("realm_access", Map.of("roles", List.of("admin", "investigator", "offline_access")))
			.build();

		// Call service
		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(new OidcUserAuthority(idToken)));

		// Test results
		assertTrue(containsAuthority(mapped, "ROLE_admin"));
		assertTrue(containsAuthority(mapped, "ROLE_investigator"));
		// Roles unknown to Karnak are not mapped
		assertEquals(3, mapped.size());
	}

	@Test
	void should_map_client_roles_of_user_info_to_karnak_roles() {
		// Init data
		OidcIdToken idToken = OidcIdToken.withTokenValue("token").claim("sub", "user-id").build();
		OidcUserInfo userInfo = OidcUserInfo.builder()
			.subject("user-id")
			.claim("resource_access", Map.of("karnak", Map.of("roles", List.of("user"))))
			.build();

		// Call service
		Collection<? extends GrantedAuthority> mapped = mapper
			.mapAuthorities(List.of(new OidcUserAuthority(idToken, userInfo)));

		// Test results
		assertTrue(containsAuthority(mapped, "ROLE_user"));
	}

	@Test
	void should_map_roles_of_oauth2_user_attributes() {
		// Init data
		OAuth2UserAuthority authority = new OAuth2UserAuthority(
				Map.of("realm_access", Map.of("roles", List.of("admin"))));

		// Call service
		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(authority));

		// Test results
		assertTrue(containsAuthority(mapped, "ROLE_admin"));
	}

	@Test
	void should_keep_original_authorities() {
		// Init data
		SimpleGrantedAuthority original = new SimpleGrantedAuthority("OIDC_USER");

		// Call service
		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(original));

		// Test results
		assertEquals(1, mapped.size());
		assertTrue(containsAuthority(mapped, "OIDC_USER"));
	}

	@Test
	void when_no_role_claim_should_not_add_authorities() {
		// Init data
		OidcIdToken idToken = OidcIdToken.withTokenValue("token").claim("sub", "user-id").build();

		// Call service
		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(new OidcUserAuthority(idToken)));

		// Test results
		assertEquals(1, mapped.size());
	}

	@Test
	void should_merge_realm_and_client_roles_across_multiple_clients() {
		OidcIdToken idToken = OidcIdToken.withTokenValue("token")
			.claim("sub", "user-id")
			.claim("realm_access", Map.of("roles", List.of("admin")))
			.claim("resource_access",
					Map.of("karnak", Map.of("roles", List.of("user")), "other-client",
							Map.of("roles", List.of("investigator"))))
			.build();

		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(new OidcUserAuthority(idToken)));

		assertTrue(containsAuthority(mapped, "ROLE_admin"));
		assertTrue(containsAuthority(mapped, "ROLE_user"));
		assertTrue(containsAuthority(mapped, "ROLE_investigator"));
	}

	@Test
	void should_ignore_non_string_role_entries() {
		OidcIdToken idToken = OidcIdToken.withTokenValue("token")
			.claim("sub", "user-id")
			.claim("realm_access", Map.of("roles", List.of("admin", 42, true)))
			.build();

		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(new OidcUserAuthority(idToken)));

		// Only the valid string role is mapped (plus the original OIDC authority).
		assertTrue(containsAuthority(mapped, "ROLE_admin"));
		assertEquals(2, mapped.size());
	}

	@Test
	void should_ignore_a_malformed_resource_access_claim() {
		OidcIdToken idToken = OidcIdToken.withTokenValue("token")
			.claim("sub", "user-id")
			.claim("resource_access", "not-a-map")
			.build();

		Collection<? extends GrantedAuthority> mapped = mapper.mapAuthorities(List.of(new OidcUserAuthority(idToken)));

		assertEquals(1, mapped.size());
	}

	@Test
	void should_return_an_empty_collection_for_no_authorities() {
		assertTrue(mapper.mapAuthorities(List.of()).isEmpty());
	}

	private static boolean containsAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
		return authorities.stream().anyMatch(ga -> ga.getAuthority().equals(authority));
	}

}
