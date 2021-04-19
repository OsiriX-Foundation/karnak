/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import org.karnak.backend.cache.RequestCache;
import org.karnak.backend.security.OpenIdConnectLogoutHandler;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(value = "IDP", havingValue = "oidc")
public class SecurityOpenIdConnectConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // Uses RequestCache to track unauthorized requests so that users are redirected
        // appropriately after login
        .requestCache()
        .requestCache(new RequestCache())
        // Disables cross-site request forgery (CSRF) protection for main route
        .and()
        .csrf()
        .ignoringAntMatchers("/")
        // Turns on authorization
        .and()
        .authorizeRequests()
        // Allows all internal traffic from the Vaadin framework
        .requestMatchers(SecurityUtil::isFrameworkInternalRequest)
        .permitAll()
        // Allows all authenticated traffic
        // .antMatchers("/*").hasAuthority(SecurityRole.ADMIN_ROLE.getType())
        .anyRequest()
        .authenticated()
        // OpenId connect login
        .and()
        .oauth2Login()
        // Handle logout
        .and()
        .logout()
        .addLogoutHandler(new OpenIdConnectLogoutHandler());
  }

  @Override
  public void configure(WebSecurity web) {
    // Access to static resources, bypassing Spring security.
    web.ignoring()
        .antMatchers(
            "/VAADIN/**",
            // the standard favicon URI
            "/favicon.ico",
            // the robots exclusion standard
            "/robots.txt",
            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            // icons and images
            "/icons/**",
            "/images/**",
            "/styles/**",
            "/img/**",
            // (development mode) H2 debugging console
            "/h2-console/**");
  }
}
