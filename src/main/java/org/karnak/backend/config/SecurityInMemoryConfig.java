package org.karnak.backend.config;

import org.karnak.backend.cache.RequestCache;
import org.karnak.backend.enums.SecurityRole;
import org.karnak.backend.security.DefaultIdpLoadCondition;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
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

  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error";
  private static final String LOGIN_URL = "/login";
  private static final String LOGOUT_SUCCESS_URL = "/login";

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        // Uses RequestCache to track unauthorized requests so that users are redirected
        // appropriately after login
        .requestCache()
        .requestCache(new RequestCache())
        // Disables cross-site request forgery (CSRF) protection for main route and login
        .and()
        .csrf().ignoringAntMatchers("/", "/login")
        // Turns on authorization
        .and()
        .authorizeRequests()
        // Allows all internal traffic from the Vaadin framework
        .requestMatchers(SecurityUtil::isFrameworkInternalRequest)
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
        .loginProcessingUrl(LOGIN_PROCESSING_URL)
        .failureUrl(LOGIN_FAILURE_URL)
        // Configures the logout URL
        .and()
        .logout()
        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
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
            .roles(SecurityRole.ADMIN_ROLE.getType(), SecurityRole.USER_ROLE.getType());
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
}