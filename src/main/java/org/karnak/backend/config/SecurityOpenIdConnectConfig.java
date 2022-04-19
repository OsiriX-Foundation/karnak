/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.karnak.backend.cache.RequestCache;
import org.karnak.backend.constant.Token;
import org.karnak.backend.security.OpenIdConnectLogoutHandler;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(value = "IDP", havingValue = "oidc")
public class SecurityOpenIdConnectConfig extends WebSecurityConfigurerAdapter {

	@Value("${spring.security.oauth2.client.provider.keycloak.jwk-set-uri}")
	private String jwkSetUri;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// Uses RequestCache to track unauthorized requests so that users are
				// redirected
				// appropriately after login
				.requestCache().requestCache(new RequestCache())
				// Disables cross-site request forgery (CSRF) protection for main route
				.and().csrf().ignoringAntMatchers("/")
				// Turns on authorization
				.and().authorizeRequests()
				// Allows all internal traffic from the Vaadin framework
				.requestMatchers(SecurityUtil::isFrameworkInternalRequest).permitAll()
				// Allow get echo endpoint
				.antMatchers(HttpMethod.GET, "/api/echo/destinations").permitAll()
				// Allows all authenticated traffic
				.anyRequest().authenticated()
				// OpenId connect login
				.and().oauth2Login(oauth2Login -> oauth2Login.userInfoEndpoint(
						// Extract roles from access token
						userInfoEndpoint -> userInfoEndpoint.oidcUserService(oidcUserService())))
				// Handle logout
				.logout().addLogoutHandler(new OpenIdConnectLogoutHandler());
	}

	@Override
	public void configure(WebSecurity web) {
		// Access to static resources, bypassing Spring security.
		web.ignoring().antMatchers("/VAADIN/**",
				// the standard favicon URI
				"/favicon.ico",
				// web application manifest
				"/manifest.webmanifest", "/sw.js", "/offline.html", "/sw-runtime-resources-precache.js",
				// icons and images
				"/icons/logo**", "/img/karnak.png");
	}

	/**
	 * Retrieve roles from access token
	 * @param jwt access token
	 * @return Roles found
	 */
	private Set<SimpleGrantedAuthority> retrieveRolesFromAccessToken(Jwt jwt) {
		// Build roles
		return ((List<String>) ((Map<String, Object>) ((Map<String, Object>) jwt.getClaims().get(Token.RESOURCE_ACCESS))
				.get(Token.RESOURCE_NAME)).get(Token.ROLES)).stream().map(roleName -> Token.PREFIX_ROLE + roleName)
						.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

	/**
	 * Decode access token
	 * @param accessToken Access token to decode
	 * @return access token decoded
	 */
	private Jwt decodeAccessToken(OAuth2AccessToken accessToken) {
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build().decode(accessToken.getTokenValue());
	}

	/**
	 * Extract roles from access token and set authorities in the authenticated user
	 * @return OAuth2UserService
	 */
	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService oidcUserService = new OidcUserService();
		return userRequest -> {
			// Get the user from the request
			OidcUser oidcUser = oidcUserService.loadUser(userRequest);

			// Decode access token
			Jwt jwt = decodeAccessToken(userRequest.getAccessToken());

			// Retrieve roles from access token
			Set<SimpleGrantedAuthority> grantedAuthoritiesFromAccessToken = retrieveRolesFromAccessToken(jwt);

			// Update the user with roles found
			return new DefaultOidcUser(grantedAuthoritiesFromAccessToken, oidcUser.getIdToken(),
					oidcUser.getUserInfo());
		};
	}

}
