/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import org.karnak.backend.cache.RequestCache;
import org.karnak.backend.enums.SecurityRole;
import org.karnak.backend.security.DefaultIdpLoadCondition;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@Conditional(value = DefaultIdpLoadCondition.class)
public class SecurityInMemoryConfig extends WebSecurityConfigurerAdapter {

  private static final String LOGIN_FAILURE_URL = "/login?error";

  private static final String LOGIN_URL = "/login";

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        // Uses RequestCache to track unauthorized requests so that users are
        // redirected
        // appropriately after login
        .requestCache()
        .requestCache(new RequestCache())
        // Disables cross-site request forgery (CSRF) protection for main route
        // and login
        .and()
        .csrf()
        .ignoringAntMatchers("/", LOGIN_URL)
        // Turns on authorization
        .and()
        .authorizeRequests()
        // Actuator and health
        .antMatchers("/actuator/**").permitAll()
        .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
        // Allows all internal traffic from the Vaadin framework
        .requestMatchers(SecurityUtil::isFrameworkInternalRequest)
        .permitAll()
        // Allow get echo endpoint
        .antMatchers(HttpMethod.GET, "/api/echo/destinations")
        .permitAll()
        // Allows all authenticated traffic
        .antMatchers("/*")
        .hasRole(SecurityRole.ADMIN_ROLE.getType())
        .anyRequest()
        .authenticated()
        // Enables form-based login and permits unauthenticated access to it
        .and()
        .formLogin()
        // Configures the login page URLs
        .loginPage(LOGIN_URL)
        .permitAll()
        .loginProcessingUrl(LOGIN_URL)
        .failureUrl(LOGIN_FAILURE_URL)
        // Configures the logout URL
        .and()
        .logout()
        .logoutSuccessUrl(LOGIN_URL)
        .and()
        .exceptionHandling()
        .accessDeniedPage(LOGIN_URL);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Configure users and roles in memory
    auth.inMemoryAuthentication()
        .withUser(AppConfig.getInstance().getKarnakadmin())
        .password("{noop}" + AppConfig.getInstance().getKarnakpassword())
        .roles(
            SecurityRole.ADMIN_ROLE.getType(),
            SecurityRole.INVESTIGATOR_ROLE.getType(),
            SecurityRole.USER_ROLE.getType());
  }

  @Override
  public void configure(WebSecurity web) {
    // Access to static resources, bypassing Spring security.
    web.ignoring()
        .antMatchers(
            "/VAADIN/**",
            // the standard favicon URI
            "/favicon.ico",
            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/sw-runtime-resources-precache.js",
            // icons and images
            "/icons/logo**",
            "/img/karnak.png");
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public RequestCache requestCache() { //
    return new RequestCache();
  }
}
