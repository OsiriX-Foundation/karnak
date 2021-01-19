/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.config;

import java.io.InputStream;
import java.net.URL;
import javax.annotation.PostConstruct;
import org.karnak.backend.cache.ExternalIDCache;
import org.karnak.backend.cache.MainzellisteCache;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.data.repo.ProfileRepo;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.backend.model.standard.ConfidentialityProfiles;
import org.karnak.backend.model.standard.StandardDICOM;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;
import org.karnak.backend.service.profilepipe.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AppConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

  private static AppConfig instance;
  private String environment;
  private String name;
  private String karnakadmin;
  private String karnakpassword;

  @Autowired private ProfileRepo profileRepo;

  public static AppConfig getInstance() {
    return instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKarnakadmin() {
    return karnakadmin;
  }

  public void setKarnakadmin(String karnakadmin) {
    this.karnakadmin = karnakadmin;
  }

  public String getKarnakpassword() {
    return karnakpassword;
  }

  public void setKarnakpassword(String karnakpassword) {
    this.karnakpassword = karnakpassword;
  }

  public ProfileRepo getProfilePersistence() {
    return profileRepo;
  }

  @Bean("ConfidentialityProfiles")
  public ConfidentialityProfiles getConfidentialityProfile() {
    return new ConfidentialityProfiles();
  }

  @Bean("ExternalIDPatient")
  public PatientClient getExternalIDCache() {
    return new ExternalIDCache();
  }

  @Bean("MainzellisteCache")
  public PatientClient getMainzellisteCache() {
    return new MainzellisteCache();
  }

  // https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
  @EventListener(ApplicationReadyEvent.class)
  public void setProfilesByDefault() {
    URL profileURL = Profiles.class.getResource("profileByDefault.yml");
    if (profileRepo.existsByNameAndBydefault("Dicom Basic Profile", true) == false) {
      try (InputStream inputStream = profileURL.openStream()) {
        final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
        final ProfilePipeBody profilePipeYml = yaml.load(inputStream);
        final ProfilePipeService profilePipeService = new ProfilePipeServiceImpl();
        profilePipeService.saveProfilePipe(profilePipeYml, true);
      } catch (final Exception e) {
        LOGGER.error("Cannot persist default profile {}", profileURL, e);
      }
    }
  }

  @Bean("StandardDICOM")
  public StandardDICOM getStandardDICOM() {
    return new StandardDICOM();
  }
}
