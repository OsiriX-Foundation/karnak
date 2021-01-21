/*
* Copyright (c) 2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.model;

public class NotificationSetUp {

  private final String notifyObjectErrorPrefix;
  private final String notifyObjectPattern;
  private final String[] notifyObjectValues;
  private final int notifyInterval;

  public NotificationSetUp(
      String notifyObjectErrorPrefix,
      String notifyObjectPattern,
      String[] notifyObjectValues,
      int notifyInterval) {
    super();
    this.notifyObjectErrorPrefix = notifyObjectErrorPrefix;
    this.notifyObjectPattern = notifyObjectPattern;
    this.notifyObjectValues = notifyObjectValues;
    this.notifyInterval = notifyInterval;
  }

  public String getNotifyObjectErrorPrefix() {
    return notifyObjectErrorPrefix;
  }

  public String getNotifyObjectPattern() {
    return notifyObjectPattern;
  }

  public String[] getNotifyObjectValues() {
    return notifyObjectValues;
  }

  public int getNotifyInterval() {
    return notifyInterval;
  }
}
