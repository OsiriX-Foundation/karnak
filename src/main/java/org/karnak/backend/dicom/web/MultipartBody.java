/*
 * Copyright (c) 2020 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipartBody implements Flow.Subscriber<ByteBuffer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultipartBody.class);
  /** @see <a href="https://tools.ietf.org/html/rfc2387">multipart specifications</a> */
  private final String boundary;

  private final ContentType contentType;
  private final List<Part> parts = new ArrayList<>();
  private final AtomicBoolean closed = new AtomicBoolean(false);

  private Flow.Subscription subscription;

  public MultipartBody(ContentType contentType, String boundary) {
    this.contentType = contentType;
    this.boundary = boundary;
  }

  public HttpRequest.BodyPublisher bodyPublisher(Supplier<? extends InputStream> streamSupplier) {
    return HttpRequest.BodyPublishers.ofInputStream(streamSupplier);
  }

  public HttpRequest.BodyPublisher bodyPublisher() {
    return HttpRequest.BodyPublishers.ofInputStream(() -> new SequenceInputStream(enumeration()));
  }

  byte[] getHeader(Part part) {
    if (closed.get()) {
      return null;
    }
    return part.header(boundary).getBytes(StandardCharsets.UTF_8);
  }

  InputStream getHeaderStream(Part part) {
    if (closed.get()) {
      return null;
    }
    return new ByteArrayInputStream(getHeader(part));
  }

  byte[] getEnd() {
    if (closed.getAndSet(true)) {
      return null;
    }
    return closeDelimiter().getBytes(StandardCharsets.UTF_8);
  }

  InputStream getEndStream() {
    if (closed.getAndSet(true)) {
      return null;
    }
    return new ByteArrayInputStream(closeDelimiter().getBytes(StandardCharsets.UTF_8));
  }

  public List<Part> getParts() {
    return parts;
  }

  Enumeration<? extends InputStream> enumeration() {
    return new Enumeration<>() {
      Iterator<Part> iter = parts.iterator();
      Part part;
      boolean closed;

      @Override
      public boolean hasMoreElements() {
        return !closed;
      }

      @Override
      public InputStream nextElement() {
        InputStream stream;
        if (part != null) {
          stream = part.newInputStream();
          part = null;
        } else if (iter.hasNext()) {
          part = iter.next();
          stream = getHeaderStream(part);
        } else if (!closed) {
          stream = getEndStream();
          closed = true;
        } else {
          throw new NoSuchElementException();
        }
        return stream;
      }
    };
  }

  String closeDelimiter() {
    return "\r\n--" + boundary + "--";
  }

  public String getBoundary() {
    return boundary;
  }

  public void addPart(String type, final byte[] b, String location) {
    addPart(
        type,
        new Payload() {
          @Override
          public long size() {
            return b.length;
          }

          @Override
          public InputStream newInputStream() {
            return new ByteArrayInputStream(b);
          }
        },
        location);
  }

  public void addPart(String type, final Path path, String location) {
    addPart(
        type,
        new Payload() {
          @Override
          public long size() {
            try {
              return Files.size(path);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          }

          @Override
          public InputStream newInputStream() {
            try {
              return Files.newInputStream(path);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          }
        },
        location);
  }

  public void addPart(String type, Payload payload, String location) {
    parts.add(new Part(payload, type, location));
  }

  public String contentType() {
    return "multipart/related;type=\"" + contentType.type + "\";boundary=" + boundary;
  }

  private Part firstPart() {
    return parts.iterator().next();
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(ByteBuffer item) {
    subscription.request(1);
  }

  @Override
  public void onError(Throwable throwable) {
    throwable.printStackTrace();
  }

  @Override
  public void onComplete() {
    LOGGER.info("Flow complete");
    closed.set(true);
  }

  public void reset() {
    parts.clear();
    closed.set(false);
  }

  static class Part {
    final String type;
    final String location;
    final Payload payload;

    public Part(Payload payload, String type, String location) {
      this.type = type;
      this.location = location;
      this.payload = payload;
    }

    public InputStream newInputStream() {
      return payload.newInputStream();
    }

    String header(String boundary) {
      StringBuilder sb =
          new StringBuilder(256)
              .append("\r\n--")
              .append(boundary)
              .append("\r\nContent-Type: ")
              .append(type);
      if (payload.size() < 0) {
        sb.append("\r\nContent-Encoding: ").append("gzip, identity");
      } else {
        sb.append("\r\nContent-Length: ").append(payload.size());
      }
      if (location != null) {
        sb.append("\r\nContent-Location: ").append(location);
      }
      return sb.append("\r\n\r\n").toString();
    }

    void prompt(String boundary) {
      LOGGER.debug("> --" + boundary);
      LOGGER.debug("> Content-Type: " + type);
      if (payload.size() < 0) {
        LOGGER.debug("> Content-Encoding: gzip, identity");
      } else {
        LOGGER.debug("> Content-Length: " + payload.size());
      }
      if (location != null) LOGGER.debug("> Content-Location: " + location);
      LOGGER.debug(">");
      LOGGER.debug("> [...]");
    }
  }

  void prompt() {
    parts.stream().forEach(p -> p.prompt(boundary));
    LOGGER.debug("> --" + boundary + "--");
    LOGGER.debug(">");
  }
}
