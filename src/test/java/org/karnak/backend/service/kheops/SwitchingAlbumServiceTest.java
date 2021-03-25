/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.kheops;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.internal.DicomObjectImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SwitchingAlbumServiceTest {

  // Service
  private SwitchingAlbumService switchingAlbumService;

  @BeforeEach
  public void setUp() {

    // Build mocked service
    switchingAlbumService = new SwitchingAlbumService();
  }

  @Test
  void should_() {
    // Init data
    DestinationEntity destinationEntity = new DestinationEntity();
    KheopsAlbumsEntity kheopsAlbumsEntity = new KheopsAlbumsEntity();
    DicomObject dicomObject = new DicomObjectImpl();
    dicomObject.setString(Tag.StudyInstanceUID, VR.SH, "studyInstanceUID");
    dicomObject.setString(Tag.SeriesInstanceUID, VR.SH, "seriesInstanceUID");
    dicomObject.setString(Tag.SOPInstanceUID, VR.SH, "sopInstanceUID");
    kheopsAlbumsEntity.setAuthorizationSource("authorizationSource");
    kheopsAlbumsEntity.setAuthorizationDestination("authorizationDestination");
    kheopsAlbumsEntity.setCondition("true");
    destinationEntity.setDesidentification(true);
    ProjectEntity projectEntity = new ProjectEntity();
    byte[] tabByte = new byte[10];
    tabByte[0] = 1;
    projectEntity.setSecret(tabByte);
    destinationEntity.setProjectEntity(projectEntity);
    kheopsAlbumsEntity.setUrlAPI("http://karnak.com");
    kheopsAlbumsEntity.setId(1L);

    // Call service
    switchingAlbumService.apply(destinationEntity, kheopsAlbumsEntity, dicomObject);

    // Test results

  }
}
