/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.gateway;

import java.io.File;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.karnak.backend.dicom.DicomGateway;
import org.karnak.backend.dicom.GatewayParams;
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

@Service
public class GatewayService implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);

  private final GatewaySetUpService gatewaySetUpService;
  private DicomGateway gateway;

  @Autowired
  public GatewayService(final GatewaySetUpService gatewaySetUpService) {
    this.gatewaySetUpService = gatewaySetUpService;
  }

  private static DicomGateway buildDicomGateway(GatewaySetUpService config) {
    DicomGateway gateway;
    try {
      String[] acceptedCallingAETitles =
          GatewayParams.getAcceptedCallingAETitles(config.getDestinations());
      GatewayParams gparams =
          new GatewayParams(config.getAdvancedParams(), false, null, acceptedCallingAETitles);
      gateway = new DicomGateway(config.getDestinations());
      gateway.start(config.getCallingDicomNode(), gparams);
      LOGGER.info("Karnak DICOM gateway servlet is running: {}", config);
      return gateway;
    } catch (Exception e) {
      LOGGER.error("Cannot start DICOM gateway", e);
      return null;
    }
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOGGER.info("Application Event: {}", event);
  }

  @EventListener
  public void reloadOutboundNodes(NodeEvent event) {
    gatewaySetUpService.update(event);
  }

  @PreDestroy
  public void destroy() {
    if (gateway != null) {
      gateway.stop();
    }
    LOGGER.info("{}", "Gateway has been stopped");
    String dir = System.getProperty("dicom.native.codec");
    if (StringUtil.hasText(dir)) {
      FileUtil.delete(new File(dir));
    }
  }

  private void initGateway() {
    gateway = buildDicomGateway(gatewaySetUpService);
  }

  @PostConstruct
  public void init() {
    LOGGER.info("{}", "Start the gateway manager running as a background process");
    try {
      URL resource = this.getClass().getResource("/lib");
      NativeLibraryManager.initNativeLibs(resource);
    } catch (Exception e1) {
      throw new IllegalStateException("Cannot register DICOM native librairies", e1);
    }
    initGateway();
  }
}
