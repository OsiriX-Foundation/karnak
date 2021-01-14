package org.karnak.backend.config;

import org.karnak.backend.cache.RequestCache;
import org.karnak.backend.util.SecurityUtil;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@ConditionalOnProperty(value = "IDP", havingValue = "keycloak")
public class SecurityKeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

  private static final String LOGOUT_SUCCESS_URL = "/mainLayout";

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(keycloakAuthenticationProvider());
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

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
        // Configures the logout URL
        .and()
        .logout()
        .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
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

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public RequestCache requestCache() { //
    return new RequestCache();
  }

  @Bean
  public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }
}
