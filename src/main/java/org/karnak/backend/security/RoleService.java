/*
 * Copyright (c) 2021 Karnak Team and other contributors.
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
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

/** Role Service */
@Service
public class RoleService {

  // Client service
  private final OAuth2AuthorizedClientService clientService;

  @Value("${spring.security.oauth2.client.provider.keycloak.jwk-set-uri}")
  private String jwkSetUri;

  /**
   * Autowired constructor
   *
   * @param clientService Client service
   */
  @Autowired
  public RoleService(OAuth2AuthorizedClientService clientService) {
    this.clientService = clientService;
  }

  /** Update roles depending on the access token */
  public void updateAccessTokenRoles() {

    // Retrieve the current authentication
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      // Retrieve authentication
      OAuth2AuthenticationToken oAuth2AuthenticationToken =
          (OAuth2AuthenticationToken) authentication;

      // Retrieve Access token
      Jwt jwt = retrieveAccessToken(oAuth2AuthenticationToken);

      // Retrieve roles from access token
      List<SimpleGrantedAuthority> accessTokenAuthorities = retrieveRolesFromAccessToken(jwt);

      // Get previous roles
      Collection<SimpleGrantedAuthority> oldAuthorities =
          (Collection<SimpleGrantedAuthority>)
              SecurityContextHolder.getContext().getAuthentication().getAuthorities();

      // Set of roles to update
      Set<SimpleGrantedAuthority> updatedAuthorities = new HashSet<>();
      updatedAuthorities.addAll(accessTokenAuthorities);
      updatedAuthorities.addAll(oldAuthorities);

      // Set roles in the context
      SecurityContextHolder.getContext()
          .setAuthentication(
              new OAuth2AuthenticationToken(
                  (OAuth2User)
                      SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                  updatedAuthorities,
                  oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()));
    }
  }

  /**
   * Retrieve roles from access token
   *
   * @param jwt access token
   * @return Roles found
   */
  private List<SimpleGrantedAuthority> retrieveRolesFromAccessToken(Jwt jwt) {
    // Build roles
    return ((List<String>)
            ((Map<String, Object>)
                    ((Map<String, Object>) jwt.getClaims().get("resource_access")).get("karnak"))
                .get("roles"))
        .stream()
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }

  /**
   * Retrieve access token from authentication
   *
   * @param oAuth2AuthenticationToken authentication
   * @return access token
   */
  private Jwt retrieveAccessToken(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
    OAuth2AuthorizedClient client =
        clientService.loadAuthorizedClient(
            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
            oAuth2AuthenticationToken.getName());
    NimbusJwtDecoder build = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    return build.decode(client.getAccessToken().getTokenValue());
  }
}
