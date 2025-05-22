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

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.karnak.backend.cache.ExternalIDCache;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.data.repo.ProfileRepo;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.backend.model.standard.ConfidentialityProfiles;
import org.karnak.backend.model.standard.StandardDICOM;
import org.karnak.backend.service.profilepipe.Profile;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@EnableCaching
@Slf4j

public class AppConfig {

	private static AppConfig instance;

	private String environment;

	private String name;

	private String karnakadmin;

	private String karnakpassword;

	private final ProfileRepo profileRepo;

	private final ProfilePipeService profilePipeService;

	private final ExternalIDCache externalIDCache;

	@Autowired
	public AppConfig(final ProfileRepo profileRepo, final ProfilePipeService profilePipeService,
			final ExternalIDCache externalIDCache) {
		this.profileRepo = profileRepo;
		this.profilePipeService = profilePipeService;
		this.externalIDCache = externalIDCache;
	}

	@PostConstruct
	public void postConstruct() {
		instance = this;
	}

	public static AppConfig getInstance() {
		return instance;
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
		return karnakadmin != null ? karnakadmin : "admin";
	}

	public void setKarnakadmin(String karnakadmin) {
		this.karnakadmin = karnakadmin;
	}

	public String getKarnakpassword() {
		return karnakpassword != null ? karnakpassword : "admin";
	}

	public void setKarnakpassword(String karnakpassword) {
		this.karnakpassword = karnakpassword;
	}

	@Bean("ConfidentialityProfiles")
	public ConfidentialityProfiles getConfidentialityProfile() {
		return new ConfidentialityProfiles();
	}

	@Bean("ExternalIDPatient")
	public PatientClient getExternalIDCache() {
		return externalIDCache;
	}

	// https://stackoverflow.com/questions/27405713/running-code-after-spring-boot-starts
	@EventListener(ApplicationReadyEvent.class)
	public void setProfilesByDefault() {
		URL profileURL = Profile.class.getResource("profileByDefault.yml");
		if (!profileRepo.existsByNameAndByDefault("Dicom Basic Profile", true)) {
			try (InputStream inputStream = profileURL.openStream()) {
				final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class, new LoaderOptions()));
				final ProfilePipeBody profilePipeYml = yaml.load(inputStream);
				profilePipeService.saveProfilePipe(profilePipeYml, true);
			}
			catch (final Exception e) {
				log.error("Cannot persist default profile {}", profileURL, e);
			}
		}
	}

	@Bean("StandardDICOM")
	public StandardDICOM getStandardDICOM() {
		return new StandardDICOM();
	}
}
