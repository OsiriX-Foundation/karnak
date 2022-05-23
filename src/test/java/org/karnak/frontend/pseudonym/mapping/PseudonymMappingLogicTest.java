/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.cache.ExternalIDCache;
import org.karnak.backend.cache.Patient;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectService;
import org.karnak.backend.service.PseudonymMappingService;
import org.mockito.Mockito;

class PseudonymMappingLogicTest {

  // Service
  private PseudonymMappingLogic pseudonymMappingLogic;

  private final PseudonymMappingService pseudonymMappingServiceMock =
      Mockito.mock(PseudonymMappingService.class);

  private final ExternalIDCache externalIDCacheMock = Mockito.mock(ExternalIDCache.class);

  private final ProjectService projectServiceMock = Mockito.mock(ProjectService.class);

  @BeforeEach
  void setUp() {

    // Behaviour of mocks
    // ExternalIDCacheMock
    List<Patient> pseudonymPatients = new ArrayList<>();
    Patient pseudonymPatient =
        new Patient(
            "pseudonym",
            "patientId",
            "patientFirstName",
            "patientLastName",
            "issuerOfPatientId",
            1L);
    pseudonymPatients.add(pseudonymPatient);
    Mockito.when(externalIDCacheMock.getAll()).thenReturn(pseudonymPatients);
    // ProjectService
    ProjectEntity projectEntity = new ProjectEntity();
    projectEntity.setId(1L);
    projectEntity.setName("Project");
    Mockito.when(projectServiceMock.retrieveProject(Mockito.anyLong())).thenReturn(projectEntity);

    // Build mocked service
    pseudonymMappingLogic =
        new PseudonymMappingLogic(
            pseudonymMappingServiceMock, externalIDCacheMock, projectServiceMock);
  }

  @Test
  void should_retrieve_mainzelliste_patient() {

    // Call service
    pseudonymMappingLogic.retrieveMainzellistePatient("pseudonym");

    // Test results
    Mockito.verify(pseudonymMappingServiceMock, Mockito.times(1))
        .retrieveMainzellistePatient(Mockito.anyString());
  }

  @Test
  void should_retrieve_external_id_cache_patient() {

    // Call service
    Map<String, Patient> externalIDCachePatients =
        pseudonymMappingLogic.retrieveExternalIDCachePatients("pseudonym");

    // Test results
    Mockito.verify(externalIDCacheMock, Mockito.times(1)).getAll();
    assertEquals("Project", externalIDCachePatients.keySet().stream().findFirst().get());
    assertEquals("patientId", externalIDCachePatients.get("Project").getPatientId());
  }
}
