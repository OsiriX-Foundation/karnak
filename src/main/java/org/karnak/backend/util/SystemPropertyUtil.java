/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;

public class SystemPropertyUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertyUtil.class);

  private SystemPropertyUtil() {}

  /**
   * Retrieve system property
   *
   * @param key Key
   * @param defaultValue default value
   * @return property found
   */
  public static String retrieveSystemProperty(String key, String defaultValue) {
    // TODO: to remove logs
    LOGGER.info("SystemPropertyUtil key:" + key);
    LOGGER.info("SystemPropertyUtil default value:" + defaultValue);
    String val = System.getProperty(key);
    LOGGER.info("SystemPropertyUtil property val:" + val);
    if (!StringUtil.hasText(val)) {
      val = System.getenv(key);
      LOGGER.info("SystemPropertyUtil env val:" + val);
      if (!StringUtil.hasText(val)) {
        LOGGER.info("Default:" + defaultValue);
        return defaultValue;
      }
    }
    return val;
  }
}
