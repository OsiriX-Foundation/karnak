/*
 * Copyright (c) 2019-2020 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.web.HeaderFieldValues;
import org.weasis.dicom.web.MultipartReader;

public class Multipart {
  public interface Handler {
    void readBodyPart(
        org.weasis.dicom.web.MultipartReader multipartReader,
        int partNumber,
        Map<String, String> headers)
        throws IOException;
  }

  public static final String CONTENT_TYPE = "Content-Type";
  public static final String MULTIPART_RELATED = "multipart/related";

  /** Carriage return, '\r', 0x0D, 13 in decimal */
  public static final byte CR = 0x0D;

  /** Line Feed, '\n', 0x0D, 10 in decimal */
  public static final byte LF = 0x0A;

  /** Dash, '-', 0x2D, 45 in decimal */
  public static final byte DASH = 0x2D;

  public enum Separator {
    HEADER(new byte[] {CR, LF, CR, LF}),
    FIELD(new byte[] {CR, LF}),
    BOUNDARY(new byte[] {CR, LF, DASH, DASH}),
    STREAM(new byte[] {DASH, DASH});

    private final byte[] type;

    Separator(byte[] type) {
      this.type = type;
    }

    public byte[] getType() {
      return type;
    }

    @Override
    public String toString() {
      return new String(type);
    }
  }

  public enum ContentType {
    DICOM("application/dicom"),
    XML("application/dicom+xml"),
    JSON("application/dicom+json");

    private final String type;

    ContentType(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }

    @Override
    public String toString() {
      return type;
    }
  }

  private Multipart() {}

  public static void parseMultipartRelated(
      URLConnection urlConnection, InputStream inputStream, Handler handler) throws IOException {
    String contentType = urlConnection.getContentType();
    byte[] boundary = getBoundary(contentType, MULTIPART_RELATED);
    if (boundary == null) {
      throw new IllegalStateException("Cannot find boundary of multipart");
    }
    org.weasis.dicom.web.MultipartReader multipartReader =
        new MultipartReader(inputStream, boundary);
    multipartReader.setHeaderEncoding("UTF8");

    int k = 1;
    boolean nextPart = multipartReader.skipFirstBoundary();
    while (nextPart) {
      String headersString = multipartReader.readHeaders();
      handler.readBodyPart(multipartReader, k++, getHeaders(headersString));
      nextPart = multipartReader.readBoundary();
    }
  }

  protected static Map<String, String> getHeaders(String header) {
    final Map<String, String> headers = new HashMap<>();
    int length = header.length();
    int start = 0;
    while (true) {
      int end = parseEOF(header, start);
      if (start == end) {
        break;
      }
      StringBuilder buf = new StringBuilder(header.substring(start, end));
      start = end + 2;
      while (start < length) {
        int k = start;
        while (k < length) {
          char c = header.charAt(k);
          if (c != ' ' && c != '\t') {
            break;
          }
          ++k;
        }
        if (k == start) {
          break;
        }
        end = parseEOF(header, k);
        buf.append(" ");
        buf.append(header.substring(k, end));
        start = end + 2;
      }

      String field = buf.toString();
      int index = field.indexOf(':');
      if (index == -1) {
        continue;
      }
      String name = field.substring(0, index).trim();
      String value = field.substring(field.indexOf(':') + 1).trim();

      if (headers.containsKey(name)) {
        headers.put(name, headers.get(name) + "," + value);
      } else {
        headers.put(name, value);
      }
    }

    return headers;
  }

  private static int parseEOF(String header, int pos) {
    int index = pos;
    while (true) {
      int k = header.indexOf('\r', index);
      if (k == -1 || k + 1 >= header.length()) {
        throw new IllegalStateException("No EOF found in headers");
      }
      if (header.charAt(k + 1) == '\n') {
        return k;
      }
      index = k + 1;
    }
  }

  protected static byte[] getBoundary(String respContentType, String ckeckMultipartType) {
    if (!StringUtil.hasText(respContentType)) {
      return null;
    }
    org.weasis.dicom.web.HeaderFieldValues parser = new HeaderFieldValues(respContentType);
    String boundaryStr = parser.getValue("boundary");

    if (boundaryStr == null || (ckeckMultipartType != null && !parser.hasKey(ckeckMultipartType))) {
      return null;
    }
    return boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
  }
}
