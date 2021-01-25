/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.gateway;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.util.NativeLibraryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.weasis.core.util.FileUtil;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.GatewayParams;
import org.weasis.dicom.param.ListenerParams;
import org.weasis.dicom.tool.DicomGateway;
import org.weasis.dicom.tool.DicomListener;


@Service
public class GatewayService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);

  private NativeLibraryManager manager;
  private final GatewaySetUpService gatewaySetUpService;

  private DicomGateway dicomForwardOut;

  private DicomListener dicomListenerOut;
  private PullingService httpPullIn;

    @Autowired
    public GatewayService(final GatewaySetUpService gatewaySetUpService) {
        this.gatewaySetUpService = gatewaySetUpService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Application Event:" + event.toString());
    }

    private static DicomGateway buildDicomGateway(GatewaySetUpService config) {
        DicomGateway gateway;
        try {
            String[] acceptedCallingAETitles = GatewayParams
                .getAcceptedCallingAETitles(config.getDestinations());
            GatewayParams gparams = new GatewayParams(config.getAdvancedParams(), false, null,
                acceptedCallingAETitles);
            gateway = new DicomGateway(config.getDestinations());
            gateway.start(config.getCallingDicomNode(), gparams);
            LOGGER.info("Karnak DICOM gateway servlet is running: {}", config);
            return gateway;
        } catch (Exception e) {
            LOGGER.error("Cannot start DICOM gateway", e);
            return null;
        }
    }

    private static DicomListener buildDicomListener(GatewaySetUpService config) {
        DicomListener dicomListener;
        try {
            dicomListener = new DicomListener(config.getStorePath());
            String[] acceptedCallingAETitles = GatewayParams
                .getAcceptedCallingAETitles(config.getDestinations());
            ListenerParams params = new ListenerParams(config.getAdvancedParams(), false,
                "{00020016}/{00020003}", null,
                acceptedCallingAETitles);
            dicomListener.start(config.getCallingDicomNode(), params);
            LOGGER.info("Gateway DICOM listener is running: {}", config);
            return dicomListener;
        } catch (Exception e) {
            LOGGER.error("Cannot start {}-stream DICOM listener", e);
            return null;
        }
    }

  private static DicomListener buildDicomListener(GatewaySetUp config) {
    DicomListener dicomListener;
    try {
      dicomListener = new DicomListener(config.getStorePath());
      String[] acceptedCallingAETitles =
          GatewayParams.getAcceptedCallingAETitles(config.getDestinations());
      ListenerParams params =
          new ListenerParams(
              config.getAdvancedParams(),
              false,
              "{00020016}/{00020003}",
              null,
              acceptedCallingAETitles);
      dicomListener.start(config.getCallingDicomNode(), params);
      LOGGER.info("Gateway DICOM listener is running: {}", config);
      return dicomListener;
    } catch (Exception e) {
      LOGGER.error("Cannot start {}-stream DICOM listener", e);
      return null;
    }
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOGGER.info("Application Event:" + event.toString());
  }

  @EventListener
  public void reloadOutboundNodes(NodeEvent event) {
    gatewaySetUpService.update(event);
    }

  @PreDestroy
  public void destroy() {
    if (dicomListenerOut != null) {
      try {
        dicomListenerOut.stop();
      } catch (IOException e) {
        LOGGER.error("Cannot stop DICOM listener", e);
      }
    }
    if (dicomForwardOut != null) {
      try {
        dicomForwardOut.stop();
      } catch (IOException e) {
        LOGGER.error("Cannot stop gateway", e);
      }
    }
    if (httpPullIn != null) {
      httpPullIn.stop();
    }
    LOGGER.info("{}", "Gateway has been stopped");

    String dir = System.getProperty("dicom.native.codec");
    if (StringUtil.hasText(dir)) {
      FileUtil.delete(new File(dir));
    }
  }

    private void initGateway() {
        dicomForwardOut = buildDicomGateway(gatewaySetUpService);
        // } else if (Mode.ARCHIVE.equals(outMode)) {
        // dicomListenerOut = buildDicomListener(configOut);
        // }

        // httpPullIn = new PullingService(configIn);
        // httpPullIn.start();

    }

    @PostConstruct
    public void init() {
        LOGGER.info("{}", "Start the gateway manager running as a background process");
        try {
            URL resource = this.getClass().getResource("/lib");
            manager = new NativeLibraryManager(resource);

        } catch (Exception e1) {
            throw new IllegalStateException("Cannot register DICOM native librairies", e1);
        }
        initGateway();
    }
}
