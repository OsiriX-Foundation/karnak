/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum ContentType {
  APPLICATION_DICOM("application/dicom", -1);

  final String type;
  final int bulkdataTag;

  ContentType(String type, int bulkdataTag) {
    this.type = type;
    this.bulkdataTag = bulkdataTag;
  }

  static ContentType probe(Path path) {
    try {
      String type = Files.probeContentType(path);
      if (type == null) {
        throw new IOException(
            String.format("failed to determine content type of file: '%s'", path));
      }
      switch (type.toLowerCase()) {
        case "application/dicom":
          return ContentType.APPLICATION_DICOM;
      }
      throw new UnsupportedOperationException(
          String.format("unsupported content type: '%s' of file: '%s'", type, path));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
