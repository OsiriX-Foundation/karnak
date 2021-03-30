/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.externalid.ExternalIDProvider;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalIDImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDImpl.class);

  private List<ExternalIDProvider> externalIDProviderList;

  public ExternalIDImpl(DestinationEntity destinationEntity) {

    ExternalIDProviderEntity externalIDProviderEntity =
        destinationEntity.getExternalIDProviderEntity();

    ExternalIDProvider externalIDImplPID =
        classLoader(
            "file:/home/ciccius/Documents/OsiriX-Foundation/karnak/pseudonym_jar/MainzellisteImplPID-1.0.jar",
            "org.mainzelliste.pid.MainzellisteApi");
    ExternalIDProvider externalIDImplEXTID =
        classLoader(
            "file:/home/ciccius/Documents/OsiriX-Foundation/karnak/pseudonym_jar/MainzellisteImplExtid-1.0.jar",
            "org.mainzelliste.extid.MainzellisteApi");

    externalIDProviderList = new ArrayList<>();
    externalIDProviderList.add(externalIDImplPID);
    externalIDProviderList.add(externalIDImplEXTID);
  }

  public String getExternalID(Attributes dcm, String extertnalIDType) {
    ExternalIDProvider implementation =
        externalIDProviderList.stream()
            .filter(
                externalIDProvider1 ->
                    externalIDProvider1.getExternalIDType().equals(extertnalIDType))
            .findAny()
            .orElse(null);
    if (implementation != null) {
      return implementation.getExternalID(dcm);
    }
    return null;
  }

  private static ExternalIDProvider classLoader(String path, String packagePath) {
    try (URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {new URL(path)})) {
      Class<?> clazz = urlClassLoader.loadClass(packagePath);
      Class<? extends ExternalIDProvider> pseudonymServiceClass =
          clazz.asSubclass(ExternalIDProvider.class);
      Constructor<? extends ExternalIDProvider> constructor =
          pseudonymServiceClass.getConstructor();
      return constructor.newInstance();
    } catch (Exception e) {
      LOGGER.error("Cannot not load correctly the jar", e);
    }
    return null;
  }
}
