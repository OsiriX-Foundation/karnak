/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import java.util.stream.Collectors;
import org.karnak.backend.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class HazelcastService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastService.class);

  /** Log every minutes */
  @Scheduled(fixedRate = 60000)
  public void logHazelcast() {
    LOGGER.info(
        String.format(
            "Hazelcast values for instance %s:%s",
            AppConfig.getInstance().getNameInstance(),
            AppConfig.getInstance().getExternalIDCache().getAll().stream()
                .map(Object::toString)
                .collect(Collectors.joining("***"))));
  }
}
