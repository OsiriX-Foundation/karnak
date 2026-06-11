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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.karnak.backend.enums.SecurityRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

/**
 * Maps the roles provided by the OpenID Connect identity provider to the Karnak security
 * roles ({@link SecurityRole}).
 *
 * <p>
 * Roles are read from the Keycloak claims "realm_access.roles" and
 * "resource_access.*.roles" of the ID token and the userinfo endpoint. Only the roles
 * matching a {@link SecurityRole} type (admin, investigator, user) are mapped, as
 * "ROLE_"-prefixed granted authorities. The IDP must therefore be configured to include
 * the roles in the ID token or in the userinfo response (in Keycloak: client scope
 * "roles").
 */
public class OidcRoleAuthoritiesMapper implements GrantedAuthoritiesMapper {

	private static final String REALM_ACCESS_CLAIM = "realm_access";

	private static final String RESOURCE_ACCESS_CLAIM = "resource_access";

	private static final String ROLES_CLAIM = "roles";

	@Override
	public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Set<GrantedAuthority> mappedAuthorities = new HashSet<>(authorities);
		for (GrantedAuthority authority : authorities) {
			if (authority instanceof OidcUserAuthority oidcUserAuthority) {
				mappedAuthorities.addAll(extractRoles(oidcUserAuthority.getIdToken().getClaims()));
				if (oidcUserAuthority.getUserInfo() != null) {
					mappedAuthorities.addAll(extractRoles(oidcUserAuthority.getUserInfo().getClaims()));
				}
			}
			else if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
				mappedAuthorities.addAll(extractRoles(oauth2UserAuthority.getAttributes()));
			}
		}
		return mappedAuthorities;
	}

	/**
	 * Extract the Karnak granted authorities from the realm and client roles found in the
	 * claims in parameter
	 * @param claims Claims of the ID token or of the userinfo response
	 * @return Granted authorities corresponding to the roles known by Karnak
	 */
	private static Set<GrantedAuthority> extractRoles(Map<String, Object> claims) {
		Set<String> roleNames = new HashSet<>(retrieveRoles(claims.get(REALM_ACCESS_CLAIM)));
		if (claims.get(RESOURCE_ACCESS_CLAIM) instanceof Map<?, ?> resourceAccess) {
			resourceAccess.values().forEach(clientAccess -> roleNames.addAll(retrieveRoles(clientAccess)));
		}
		return roleNames.stream()
			.map(SecurityRole::fromType)
			.filter(Objects::nonNull)
			.map(securityRole -> new SimpleGrantedAuthority(securityRole.getRole()))
			.collect(Collectors.toSet());
	}

	/**
	 * Retrieve the role names of an access claim ("realm_access" or one value of
	 * "resource_access")
	 * @param accessClaim Access claim to evaluate
	 * @return Role names found
	 */
	private static Collection<String> retrieveRoles(Object accessClaim) {
		if (accessClaim instanceof Map<?, ?> access && access.get(ROLES_CLAIM) instanceof Collection<?> roles) {
			return roles.stream().filter(String.class::isInstance).map(String.class::cast).toList();
		}
		return List.of();
	}

}
