/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import org.dcm4che6.data.DicomObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.profilepipe.ProfileService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.weasis.dicom.param.AttributeEditorContext;

@SpringBootTest
class DeIdentifyEditorServiceTest {

  // Service
  private final ProfileService profileServiceMock = Mockito.mock(ProfileService.class);
  private DeIdentifyEditorService deIdentifyEditorService;

  @BeforeEach
  public void setUp() {
    // Build mocked service
    deIdentifyEditorService = new DeIdentifyEditorService(profileServiceMock);
  }

  @Test
  void should_init_from_destination() {
    // Init data
    DestinationEntity destinationEntity = new DestinationEntity();
    ProfileEntity profileEntity = new ProfileEntity();
    ProjectEntity projectEntity = new ProjectEntity();
    projectEntity.setProfileEntity(profileEntity);
    destinationEntity.setProjectEntity(projectEntity);

    // Call service
    deIdentifyEditorService.init(destinationEntity);

    // Test results
    Mockito.verify(profileServiceMock, Mockito.times(1)).init(Mockito.any(ProfileEntity.class));
  }

  @Test
  void should_apply_to_dicom_object() {

    // Init data
    DicomObject dicomObject = Mockito.mock(DicomObject.class);
    AttributeEditorContext attributeEditorContext = Mockito.mock(AttributeEditorContext.class);
    DestinationEntity destinationEntity = new DestinationEntity();
    ProfileEntity profileEntity = new ProfileEntity();
    ProjectEntity projectEntity = new ProjectEntity();
    projectEntity.setProfileEntity(profileEntity);
    destinationEntity.setProjectEntity(projectEntity);
    deIdentifyEditorService.init(destinationEntity);

    // Call service
    deIdentifyEditorService.apply(dicomObject, attributeEditorContext);

    // Test results
    Mockito.verify(profileServiceMock, Mockito.times(1))
        .apply(
            Mockito.any(DicomObject.class),
            Mockito.any(DestinationEntity.class),
            Mockito.any(ProfileEntity.class),
            Mockito.any(AttributeEditorContext.class));
  }
}
