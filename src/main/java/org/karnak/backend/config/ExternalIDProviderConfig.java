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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.externalid.ExternalIDProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalIDProviderConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDProvider.class);

  private static ExternalIDProviderConfig instance;
  private List<ExternalIDProvider> externalIDProviderList;

  public static ExternalIDProviderConfig getInstance() {
    return instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }

  public void loadExternalIDImplClass(String directory, String classpath) {
    externalIDProviderList = new ArrayList<>();
    File pluginsDir = new File(System.getProperty("user.dir") + directory);
    for (File jar : pluginsDir.listFiles()) {
      try {
        ClassLoader loader =
            URLClassLoader.newInstance(
                new URL[] {jar.toURI().toURL()}, getClass().getClassLoader());
        Class<?> clazz = Class.forName(classpath, true, loader);
        Class<? extends ExternalIDProvider> newClass = clazz.asSubclass(ExternalIDProvider.class);
        Constructor<? extends ExternalIDProvider> constructor = newClass.getConstructor();
        externalIDProviderList.add(constructor.newInstance());
      } catch (Exception e) {
        LOGGER.error("Cannot not load correctly the jar", e);
      }
    }
  }

  @Bean
  public List<ExternalIDProvider> externalIDProviderList() {
    loadExternalIDImplClass("/externalid_providers", "org.karnak.externalid.Implementation");
    externalIDProviderList.forEach(
        externalIDProvider -> System.out.println(externalIDProvider.getExternalIDType()));
    return externalIDProviderList;
  }
}
