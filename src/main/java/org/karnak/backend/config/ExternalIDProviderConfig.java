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

  private static ExternalIDProvider classLoader(String path, String packagePath) {
    try (URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {new URL(path)})) {
      urlClassLoader.toString();
      System.out.println("1");
      Class<?> clazz = urlClassLoader.loadClass(packagePath);
      System.out.println("2");
      Class<? extends ExternalIDProvider> pseudonymServiceClass =
          clazz.asSubclass(ExternalIDProvider.class);
      System.out.println("3");
      Constructor<? extends ExternalIDProvider> constructor =
          pseudonymServiceClass.getConstructor();
      System.out.println("4");
      return constructor.newInstance();
    } catch (Exception e) {
      LOGGER.error("Cannot not load correctly the jar", e);
    }
    return null;
  }

  private ExternalIDProvider load(String path, String packagePath) {
    try (URLClassLoader child =
        new URLClassLoader(new URL[] {new URL(path)}, this.getClass().getClassLoader())) {

      System.out.println("1");

      Class classToLoad = Class.forName(packagePath, true, child);
      Class<?> instance = classToLoad.getClass();
      System.out.println("2");

      Class<? extends ExternalIDProvider> pseudonymServiceClass =
          instance.asSubclass(ExternalIDProvider.class);
      Constructor<? extends ExternalIDProvider> constructor =
          pseudonymServiceClass.getConstructor();
      System.out.println("4");
      return constructor.newInstance();
    } catch (Exception e) {
      LOGGER.error("Cannot not load correctly the jar", e);
    }
    return null;
  }

  @Bean
  public List<ExternalIDProvider> externalIDProviderList() {
    /*ExternalIDProvider externalIDImplPID =
        classLoader(
            "file:/home/ciccius/Documents/OsiriX-Foundation/karnak/pseudonym_jar/MainzellisteImplPID-1.0.jar",
            "org.mainzelliste.pid.MainzellisteApi");
    ExternalIDProvider externalIDImplEXTID =
        classLoader(
            "file:/home/ciccius/Documents/OsiriX-Foundation/karnak/pseudonym_jar/MainzellisteImplExtid-1.0.jar",
            "org.mainzelliste.extid.MainzellisteApi");*/
    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    ExternalIDProvider externalIDImplPID =
        load(
            "file:pseudonym_jar/MainzellisteImplPID-1.0.jar",
            "org.mainzelliste.pid.MainzellisteApi");
    ExternalIDProvider externalIDImplEXTID =
        load(
            "file:pseudonym_jar/MainzellisteImplExtid-1.0.jar",
            "org.mainzelliste.extid.MainzellisteApi");
    System.out.println("YEAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
    externalIDProviderList = new ArrayList<>();
    externalIDProviderList.add(externalIDImplPID);
    externalIDProviderList.add(externalIDImplEXTID);
    return externalIDProviderList;
  }
}
