/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import static org.karnak.backend.enums.ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION;

import java.util.Map;
import org.externalid.ExternalIDProvider;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.frontend.forwardnode.edit.destination.DestinationLogic;
import org.karnak.frontend.forwardnode.edit.destination.component.LayoutDesidentification;

public class ExternalIDProviderUtil {

  public static ExternalIDProviderType getType(String sentence) {
    for (ExternalIDProviderType type : ExternalIDProviderType.values()) {
      if (type.getSentence().equals(sentence)) {
        return type;
      }
    }
    return null;
  }

  public static ExternalIDProviderEntity getExternalIDProviderEntityWithSentence(
      DestinationLogic destinationLogic,
      LayoutDesidentification layoutDesidentification,
      String externalIDProviderSentence) {
    for (Map.Entry<String, ExternalIDProvider> entry :
        layoutDesidentification.getExternalIDProviderImplMap().entrySet()) {
      final ExternalIDProvider externalIDProviderImpl = entry.getValue();
      final String jarName = entry.getKey();
      if (externalIDProviderImpl.getExternalIDType().equals(externalIDProviderSentence)) {
        return destinationLogic.getExteralIDProviderEntity(EXTID_PROVIDER_IMPLEMENTATION, jarName);
      }
    }
    return null;
  }
}
