/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping;

import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.service.PseudonymMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mapping logic service use to make calls to backend and implement logic linked to the mapping view
 */
@Service
public class PseudonymMappingLogic {

  private static final Logger LOGGER = LoggerFactory.getLogger(PseudonymMappingLogic.class);

  // View
  private PseudonymMappingView pseudonymMappingView;

  // Services
  private final PseudonymMappingService pseudonymMappingService;

  /**
   * Autowired constructor
   *
   * @param pseudonymMappingService Pseudonym mapping backend service
   */
  @Autowired
  public PseudonymMappingLogic(final PseudonymMappingService pseudonymMappingService) {
    this.pseudonymMappingService = pseudonymMappingService;
  }

  public PseudonymMappingView getMappingView() {
    return pseudonymMappingView;
  }

  public void setMappingView(PseudonymMappingView pseudonymMappingView) {
    this.pseudonymMappingView = pseudonymMappingView;
  }

  public MainzellistePatient retrieveMainzellistePatient(String pseudonym) {
    return pseudonymMappingService.retrieveMainzellistePatient(pseudonym);
  }
}
