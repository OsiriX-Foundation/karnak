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

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.externalid.ExternalIDProvider;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.backend.service.ExternalIDProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalIDProviderConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDProviderConfig.class);

  private static String EXTERNALID_PROVIDERS_DIRECTORY = "/externalid-providers";
  private static String CLASH_PATH = "org.karnak.ExternalIDProviderImpl";
  private static ExternalIDProviderConfig instance;
  private HashMap<String, ExternalIDProvider> externalIDProviderImplMap;
  private ExternalIDProviderService externalIDProviderService;

  @Autowired
  public ExternalIDProviderConfig(final ExternalIDProviderService externalIDProviderService) {
    this.externalIDProviderService = externalIDProviderService;
  }

  public static ExternalIDProviderConfig getInstance() {
    return instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }

  public void loadExternalIDImplClass(String directory, String classpath) {
    externalIDProviderImplMap = new HashMap<>();
    File pluginsDir = new File(System.getProperty("user.dir") + directory);
    for (File jar : pluginsDir.listFiles()) {
      try {
        ClassLoader loader =
            URLClassLoader.newInstance(
                new URL[] {jar.toURI().toURL()}, getClass().getClassLoader());
        Class<?> clazz = Class.forName(classpath, true, loader);
        Class<? extends ExternalIDProvider> newClass = clazz.asSubclass(ExternalIDProvider.class);
        Constructor<? extends ExternalIDProvider> constructor = newClass.getConstructor();
        final ExternalIDProvider externalIDProvider = constructor.newInstance();
        final String jarName = jar.getName();
        externalIDProviderImplMap.put(jarName, externalIDProvider);
        addExternalIDProviderImplInDb(jarName);
      } catch (Exception e) {
        LOGGER.error("Cannot not load correctly the jar", e);
      }
    }
  }

  private void addExternalIDProviderImplInDb(String jarName) {
    if (!externalIDProviderService.jarNameExist(jarName)) {
      final ExternalIDProviderEntity newExternalIDProviderEntity =
          new ExternalIDProviderEntity(
              false, ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION, jarName);
      externalIDProviderService.saveExternalIDProvider(newExternalIDProviderEntity);
    }
  }

  @Bean
  public HashMap<String, ExternalIDProvider> externalIDProviderImplMap() {
    loadExternalIDImplClass(EXTERNALID_PROVIDERS_DIRECTORY, CLASH_PATH);
    externalIDProviderImplMap.forEach(
        (key, externalIDProvider) -> LOGGER.warn(externalIDProvider.getDescription()));
    return externalIDProviderImplMap;
  }
}
