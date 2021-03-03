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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.function.Supplier;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DicomStowRS implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(DicomStowRS.class);
  /** @see <a href="https://tools.ietf.org/html/rfc2387">multipart specifications</a> */
  protected static final String MULTIPART_BOUNDARY = "mimeTypeBoundary";

  private static final ByteArrayInputStream emptyInputStream =
      new ByteArrayInputStream(new byte[] {});

  public enum HttpContentType {
    DICOM("application/dicom"),
    XML("application/dicom+xml"),
    JSON("application/dicom+json");

    private final String type;

    HttpContentType(String type) {
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

  private final ContentType contentType;
  private final String requestURL;
  private final String agentName;
  private final Map<String, String> headers;
  private boolean photo = false;
  private final HttpContentType type = HttpContentType.XML;
  private final MultipartBody multipartBody;
  private final HttpClient client;
  private final ExecutorService executorService;

  /**
   * @param requestURL the URL of the STOW service
   * @param contentType the value of the type in the Content-Type HTTP property
   * @param agentName the value of the User-Agent HTTP property
   * @param headers some additional header properties.
   * @throws IOException Exception during the POST initialization
   */
  public DicomStowRS(
      String requestURL, ContentType contentType, String agentName, Map<String, String> headers) {
    this.contentType = Objects.requireNonNull(contentType);
    this.requestURL = Objects.requireNonNull(getFinalUrl(requestURL), "requestURL cannot be null");
    this.headers = headers;
    this.agentName = agentName;
    this.multipartBody = new MultipartBody(ContentType.APPLICATION_DICOM, MULTIPART_BOUNDARY);
    this.executorService = Executors.newFixedThreadPool(5);
    this.client =
        HttpClient.newBuilder()
            .executor(executorService)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)) // Timeout should be an option
            .build();
  }

  private String getFinalUrl(String requestURL) {
    String url = requestURL.trim();
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    if (!url.endsWith("/studies")) {
      url += "/studies";
    }
    return url;
  }

  protected HttpRequest buildConnection(Flow.Publisher<? extends ByteBuffer> multipartSubscriber)
      throws Exception {
    ContentType partType = ContentType.APPLICATION_DICOM;
    HttpRequest.Builder builder = buidDefaultConnection();

    HttpRequest request =
        builder
            .header(
                "Content-Type",
                "multipart/related;type=\"" + partType.type + "\";boundary=" + MULTIPART_BOUNDARY)
            .POST(HttpRequest.BodyPublishers.fromPublisher(multipartSubscriber))
            .uri(new URI(requestURL))
            .build();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "> POST {} {}",
          request.uri().getRawPath(),
          request.version().orElse(HttpClient.Version.HTTP_2));
      LOGGER.debug("> Host: {}:{}", request.uri().getHost(), request.uri().getPort());
      promptHeaders("> ", request.headers());
      //            multipartBody.prompt();
    }
    return request;
  }

  private HttpRequest.Builder buidDefaultConnection() {
    HttpRequest.Builder builder = HttpRequest.newBuilder();
    if (headers != null && !headers.isEmpty()) {
      for (Entry<String, String> element : headers.entrySet()) {
        builder.header(element.getKey(), element.getValue());
      }
    }
    builder.header("Accept", type.toString());
    builder.header("User-Agent", agentName == null ? "Weasis STOWRS" : agentName);
    return builder;
  }

  protected HttpRequest buildConnection(
      Payload firstPlayLoad, Supplier<? extends InputStream> streamSupplier) throws Exception {
    ContentType partType = ContentType.APPLICATION_DICOM;
    multipartBody.addPart(partType.type, firstPlayLoad, null);

    HttpRequest.Builder builder = buidDefaultConnection();

    HttpRequest request =
        builder
            .header("Content-Type", multipartBody.contentType())
            .POST(multipartBody.bodyPublisher(streamSupplier))
            .uri(new URI(requestURL))
            .build();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "> POST {} {}",
          request.uri().getRawPath(),
          request.version().orElse(HttpClient.Version.HTTP_2));
      LOGGER.debug("> Host: {}:{}", request.uri().getHost(), request.uri().getPort());
      promptHeaders("> ", request.headers());
      multipartBody.prompt();
    }
    return request;
  }

  <T> HttpResponse<T> send(
      HttpClient client, HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler)
      throws Exception {
    HttpResponse<T> response = client.send(request, bodyHandler);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("< {} response code: {}", response.version(), response.statusCode());
      promptHeaders("< ", response.headers());
    }

    if (response.statusCode() >= 400 && response.statusCode() <= 599) {
      throw new HttpException("Error response, status code " + response.statusCode());
    }

    return response;
  }

  private static void promptHeaders(String prefix, HttpHeaders headers) {
    headers
        .map()
        .forEach((k, v) -> v.stream().forEach(v1 -> LOGGER.debug("{} {}: {}", prefix, k, v1)));
    LOGGER.debug(prefix);
  }

  @Override
  public void close() throws Exception {}

  public ContentType getContentType() {
    return contentType;
  }

  public String getRequestURL() {
    return requestURL;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public boolean isPhoto() {
    return photo;
  }

  public void uploadDicom(Path path) throws HttpException {
    Payload playload =
        new Payload() {
          @Override
          public long size() {
            return -1;
          }

          @Override
          public InputStream newInputStream() {
            try {
              return Files.newInputStream(path);
            } catch (IOException e) {
              LOGGER.error("Cannot read {}", path, e);
            }
            return emptyInputStream;
          }
        };
    uploadPayload(playload);
  }

  public void uploadDicom(InputStream in, Attributes fmi) throws HttpException {
    Payload playload =
        new Payload() {
          @Override
          public long size() {
            return -1;
          }

          @Override
          public InputStream newInputStream() {
            List<InputStream> list = new ArrayList<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DicomOutputStream dos =
                new DicomOutputStream(out, fmi.getString(Tag.TransferSyntaxUID))) {
              dos.writeFileMetaInformation(fmi);
            } catch (IOException e) {
              LOGGER.error("Cannot write fmi", e);
              return emptyInputStream;
            }
            list.add(new ByteArrayInputStream(out.toByteArray()));
            list.add(in);
            return new SequenceInputStream(Collections.enumeration(list));
          }
        };
    uploadPayload(playload);
  }

  public void uploadDicom(Attributes metadata, String tsuid) throws HttpException {
    Payload playload =
        new Payload() {
          @Override
          public long size() {
            return -1;
          }

          @Override
          public InputStream newInputStream() {
            Attributes fmi = metadata.createFileMetaInformation(tsuid);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DicomOutputStream dos = new DicomOutputStream(out, tsuid)) {
              dos.writeDataset(fmi, metadata);
            } catch (IOException e) {
              LOGGER.error("Cannot write DICOM", e);
              return emptyInputStream;
            }
            return new ByteArrayInputStream(out.toByteArray());
          }
        };
    uploadPayload(playload);
  }

  public void uploadPayload(Payload playload) throws HttpException {
    multipartBody.reset();
    try {
      HttpRequest request =
          buildConnection(playload, () -> new SequenceInputStream(multipartBody.enumeration()));
      send(client, request, HttpResponse.BodyHandlers.ofLines()).body().forEach(LOGGER::info);

      //            MultipartBody.Part part = new MultipartBody.Part(playload,
      // ContentType.APPLICATION_DICOM.type, null);
      //
      //            SubmissionPublisher<ByteBuffer> publisher = new SubmissionPublisher<>();
      //            publisher.subscribe(multipartBody);
      //            HttpRequest request = buildConnection(publisher);
      //            CompletableFuture
      //                    <HttpResponse<Stream<String>>> responses = client.sendAsync(request,
      // HttpResponse.BodyHandlers.ofLines());
      //            publisher.submit(ByteBuffer.wrap(multipartBody.getHeader(part)));
      //            publisher.submit(part.newByteBuffer());
      //            publisher.submit(ByteBuffer.wrap(multipartBody.getEnd()));
      //            publisher.close();
      //
      //            HttpResponse<Stream<String>> response = responses.get();
      //            if (LOGGER.isDebugEnabled()) {
      //                LOGGER.debug("< {} response code: {}", response.version(),
      // response.statusCode());
      //                promptHeaders("< ", response.headers());
      //            }
      //            response.body().forEach(LOGGER::info);
    } catch (HttpException httpException) {
      throw httpException;
    } catch (Exception e) {
      LOGGER.error("Cannot post DICOM", e);
    }
  }
}
