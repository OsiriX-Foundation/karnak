/*
 * Copyright (c) 2014-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

import java.io.IOException;

public class MultipartStreamException extends IOException {
  private static final long serialVersionUID = -4358358366372546933L;

  public MultipartStreamException(String message) {
    super(message);
  }

  public MultipartStreamException(String message, Throwable cause) {
    super(message, cause);
  }
}
