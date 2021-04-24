/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.api.archive;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.karnak.backend.service.gateway.AbstractGateway;
import org.karnak.backend.service.gateway.GatewaySetUpService;
import org.karnak.backend.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@WebServlet(urlPatterns = "/archive.xml")
public class ShowArchiveServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = -4229230848823235305L;
  private static final Logger LOGGER = LoggerFactory.getLogger(ShowArchiveServlet.class);

  @Autowired private GatewaySetUpService globalConfig;

  private static void scanFiles(Path aStartingDir, StringBuilder sb) {
    try (Stream<Path> walk = Files.walk(aStartingDir)) {
      walk.filter(p -> !Files.isDirectory(p) && Files.isReadable(p) && !p.endsWith(".part"))
          .forEach(p -> writeInfo(p, sb));
    } catch (IOException e) {
      LOGGER.error("Cannot check archive", e);
    }
  }

  private static void writeInfo(Path file, StringBuilder sb) {
    try (DicomInputStream in = new DicomInputStream(file.toFile())) {
      in.setIncludeBulkData(IncludeBulkData.NO);
      Attributes fmi = in.readFileMetaInformation();
      if (fmi == null
          || !fmi.containsValue(Tag.TransferSyntaxUID)
          || !fmi.containsValue(Tag.MediaStorageSOPClassUID)
          || !fmi.containsValue(Tag.MediaStorageSOPInstanceUID)) {
        Attributes ds = in.readDataset(-1, Tag.PixelData);
        fmi = ds.createFileMetaInformation(in.getTransferSyntax());
      }
      sb.append("<file tsuid=\"");
      sb.append(fmi.getString(Tag.TransferSyntaxUID));
      sb.append("\" cuid\"");
      sb.append(fmi.getString(Tag.MediaStorageSOPClassUID));
      sb.append("\" iuid\"");
      sb.append(fmi.getString(Tag.MediaStorageSOPInstanceUID));
      sb.append("\">\n");
      sb.append(file.getFileName());
      sb.append("</file>\n");
    } catch (Exception e) {
      LOGGER.error("Cannot write fmi attributes", e);
    }
  }

  @Override
  public final void init() throws ServletException {
    if (globalConfig == null) {
      LOGGER.error("ShowArchiveServlet service cannot start: GatewaySetUpService is missing.");
      destroy();
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) {
    res.setContentType("text/xml");
    PrintWriter out = null;
    try {
      out = res.getWriter();
    } catch (IOException e) {
      String errorMsg = "Cannot write response";
      LOGGER.error(errorMsg);
      ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
      return;
    }
    if (globalConfig == null) {
      String errorMsg = "Missing 'GlobalConfig' from current ServletContext";
      LOGGER.error(errorMsg);
      ServletUtil.sendResponseError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
      return;
    }

    final Path archiveDir = globalConfig.getStorePath();
    String result;
    if (archiveDir == null || !Files.isDirectory(archiveDir) || !Files.isReadable(archiveDir)) {
      result = "<archive/>";
    } else {
      StringBuilder sb = new StringBuilder("<archive>\n");
      try (Stream<Path> walk = Files.walk(archiveDir)) {
        walk.filter(p -> Files.isDirectory(p) && AbstractGateway.isFolderContainsFile(p.toFile()))
            .forEach(
                p -> {
                  sb.append("<aet name=\"");
                  sb.append(p.getFileName());
                  sb.append("\">\n");
                  scanFiles(p, sb);
                  sb.append("</aet>\n");
                });
      } catch (IOException e) {
        LOGGER.error("Cannot check archive", e);
      }

      sb.append("</archive>\n");
      result = sb.toString();
    }
    out.println(result);
  }
}
