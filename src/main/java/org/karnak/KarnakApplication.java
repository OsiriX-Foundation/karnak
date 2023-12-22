/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import java.util.Objects;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.enums.ApplicationProfile;
import org.karnak.backend.enums.EnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EntityScan("org.karnak.backend.data.entity")
@EnableJpaRepositories("org.karnak.backend.data.repo")
@EnableVaadin(value = "org.karnak")
@EnableScheduling
@EnableAsync
public class KarnakApplication implements CommandLineRunner {

	@Autowired(required = false)
	private AppConfig myConfig;

	private static final Logger log = LoggerFactory.getLogger(KarnakApplication.class);

	public static void main(String[] args) {
		SpringApplicationBuilder application = new SpringApplicationBuilder(KarnakApplication.class);

		// If environment variable IDP exists and has value "oidc": activate the profile
		// application-oidc.yml
		if (System.getenv().containsKey(EnvironmentVariable.IDP.getCode()) && Objects
			.equals(System.getenv().get(EnvironmentVariable.IDP.getCode()), ApplicationProfile.OIDC.getCode())) {
			application.profiles(ApplicationProfile.OIDC.getCode());
		}

		// Run application
		application.run(args);
	}

	@Override
	public void run(String... args) {
		log.info("StartApplication");
		log.info("using environment: " + (myConfig != null ? myConfig.getEnvironment() : ""));
		log.info("name: " + (myConfig != null ? myConfig.getName() : ""));
	}

}
