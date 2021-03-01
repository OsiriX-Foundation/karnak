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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.web.Multipart;
import org.weasis.dicom.web.MultipartStreamException;

public class MultipartReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultipartReader.class);

  public static final int HEADER_PART_MAX_SIZE = 16384;

  private final InputStream inputStream;
  private final byte[] boundary;
  private String headerEncoding;
  private int currentBoundaryLength;
  private final byte[] buffer;
  private final int bufferSize;
  private int headBuffer = 0;
  private int tailBuffer = 0;

  /**
   * @param inputStream the <code>InputStream</code> of a multipart exchange.
   * @param boundary the mark to delimit the parts of a multipart stream.
   */
  public MultipartReader(InputStream inputStream, byte[] boundary) {
    this(inputStream, boundary, 4096);
  }

  /**
   * @param inputStream the <code>InputStream</code> of a multipart exchange.
   * @param boundary the mark to delimit the parts of a multipart stream.
   * @param bufferSize the size of the buffer in bytes. Default is 4096.
   */
  public MultipartReader(InputStream inputStream, byte[] boundary, int bufferSize) {
    this.inputStream = inputStream;
    this.bufferSize = bufferSize;
    this.buffer = new byte[bufferSize];
    int blength = Multipart.Separator.BOUNDARY.getType().length;
    this.boundary = new byte[boundary.length + blength];
    this.currentBoundaryLength = boundary.length + blength;
    System.arraycopy(Multipart.Separator.BOUNDARY.getType(), 0, this.boundary, 0, blength);
    System.arraycopy(boundary, 0, this.boundary, blength, boundary.length);
  }

  public String getHeaderEncoding() {
    return headerEncoding;
  }

  public void setHeaderEncoding(String encoding) {
    headerEncoding = encoding;
  }

  public byte readByte() throws IOException {
    if (headBuffer == tailBuffer) {
      headBuffer = 0;
      tailBuffer = inputStream.read(buffer, headBuffer, bufferSize);
      if (tailBuffer == -1) {
        throw new MultipartStreamException("No more data is available");
      }
    }
    return buffer[headBuffer++];
  }

  public boolean readBoundary() throws IOException {
    headBuffer += currentBoundaryLength;

    byte[] marker = {readByte(), readByte()};
    boolean nextPart = false;
    if (compareArrays(marker, Multipart.Separator.STREAM.getType(), 2)) {
      nextPart = false;
    } else if (compareArrays(marker, Multipart.Separator.FIELD.getType(), 2)) {
      nextPart = true;
    } else {
      throw new MultipartStreamException("Unexpected bytes after the boundary separator");
    }
    return nextPart;
  }

  public String readHeaders() throws IOException {
    int k = 0;
    byte b;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int headerSize = 0;
    byte[] hsep = Multipart.Separator.HEADER.getType();
    while (k < hsep.length) {
      b = readByte();
      headerSize++;
      if (headerSize > HEADER_PART_MAX_SIZE) {
        throw new MultipartStreamException(
            "Header content is larger than "
                + HEADER_PART_MAX_SIZE
                + " bytes (max size defined in reader)");
      }
      if (b == hsep[k]) {
        k++;
      } else {
        k = 0;
      }
      baos.write(b);
    }

    String headers = null;
    if (headerEncoding != null) {
      try {
        headers = baos.toString(headerEncoding);
      } catch (UnsupportedEncodingException e) {
        LOGGER.error("Decoding header", e);
      }
    }

    if (headers == null) {
      headers = baos.toString();
    }
    return headers;
  }

  public boolean skipFirstBoundary() throws IOException {
    // Special case for the first boundary delimiter => remove CRLF
    System.arraycopy(boundary, 2, boundary, 0, boundary.length - 2);
    currentBoundaryLength = boundary.length - 2;
    try {
      discardDataBeforeDelimiter();
      return readBoundary();
    } finally {
      // Restore the original boundary
      System.arraycopy(boundary, 0, boundary, 2, boundary.length - 2);
      currentBoundaryLength = boundary.length;
      boundary[0] = Multipart.CR;
      boundary[1] = Multipart.LF;
    }
  }

  public PartInputStream newPartInputStream() {
    return new PartInputStream();
  }

  protected void discardDataBeforeDelimiter() throws IOException {
    try (InputStream in = newPartInputStream()) {
      byte[] pBuffer = new byte[1024];
      while (true) {
        if (in.read(pBuffer) == -1) {
          break;
        }
      }
    }
  }

  protected static boolean compareArrays(byte[] a, byte[] b, int count) {
    for (int i = 0; i < count; i++) {
      if (a[i] != b[i]) {
        return false;
      }
    }
    return true;
  }

  protected int findFirstBoundaryCharacter(int start) {
    for (int i = start; i < tailBuffer; i++) {
      if (buffer[i] == boundary[0]) {
        return i;
      }
    }
    return -1;
  }

  protected int findStartingBoundaryPosition() {
    int start;
    int b = 0;
    int end = tailBuffer - currentBoundaryLength;
    for (start = headBuffer; start <= end && b != currentBoundaryLength; start++) {
      start = findFirstBoundaryCharacter(start);
      if (start == -1 || start > end) {
        return -1;
      }
      for (b = 1; b < currentBoundaryLength; b++) {
        if (buffer[start + b] != boundary[b]) {
          break;
        }
      }
    }
    if (b == currentBoundaryLength) {
      return start - 1;
    }
    return -1;
  }

  public class PartInputStream extends InputStream implements AutoCloseable {
    private static final String STREAM_CLOSED_EX = "PartInputStream has been closed";

    private int position;
    private long total;
    private int offset;
    private boolean closed;

    PartInputStream() {
      moveToBoundary();
    }

    private void moveToBoundary() {
      position = findStartingBoundaryPosition();
      if (position == -1) {
        if (tailBuffer - headBuffer > boundary.length) {
          offset = boundary.length;
        } else {
          offset = tailBuffer - headBuffer;
        }
      }
    }

    private int readInputStream() throws IOException {
      if (position != -1) {
        return 0;
      }

      total += tailBuffer - headBuffer - offset;
      System.arraycopy(buffer, tailBuffer - offset, buffer, 0, offset);

      headBuffer = 0;
      tailBuffer = offset;

      while (true) {
        int readBytes = inputStream.read(buffer, tailBuffer, bufferSize - tailBuffer);
        if (readBytes == -1) {
          throw new MultipartStreamException("Unexpect end of stream");
        }

        tailBuffer += readBytes;
        moveToBoundary();
        int k = available();
        if (k > 0 || position != -1) {
          return k;
        }
      }
    }

    public long getTotal() {
      return total;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (closed) {
        throw new MultipartStreamException(STREAM_CLOSED_EX);
      }
      if (len == 0) {
        return 0;
      }
      int k = available();
      if (k == 0) {
        k = readInputStream();
        if (k == 0) {
          return -1;
        }
      }
      k = Math.min(k, len);
      System.arraycopy(buffer, headBuffer, b, off, k);
      headBuffer += k;
      total += k;
      return k;
    }

    @Override
    public int read() throws IOException {
      if (closed) {
        throw new MultipartStreamException(STREAM_CLOSED_EX);
      }
      if (available() == 0 && readInputStream() == 0) {
        return -1;
      }
      total++;
      return buffer[headBuffer++] & 0xFF;
    }

    @Override
    public int available() throws IOException {
      if (position == -1) {
        return tailBuffer - headBuffer - offset;
      }
      return position - headBuffer;
    }

    @Override
    public long skip(long bytes) throws IOException {
      if (closed) {
        throw new MultipartStreamException(STREAM_CLOSED_EX);
      }
      int k = available();
      if (k == 0) {
        k = readInputStream();
        if (k == 0) {
          return 0;
        }
      }
      long skipBytes = Math.min(k, bytes);
      headBuffer += skipBytes;
      return skipBytes;
    }

    @Override
    public void close() throws IOException {
      if (closed) {
        return;
      }

      while (true) {
        int k = available();
        if (k == 0) {
          k = readInputStream();
          if (k == 0) {
            break;
          }
        }
        skip(k); // NOSONAR no need return value when closing
      }
      closed = true;
    }

    public boolean isClosed() {
      return closed;
    }
  }
}
