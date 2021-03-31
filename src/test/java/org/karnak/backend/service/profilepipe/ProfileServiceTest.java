/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.internal.DicomObjectImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.MaskEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.enums.DestinationType;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.weasis.dicom.param.AttributeEditorContext;

@SpringBootTest
class ProfileServiceTest {

  // Services
  private ProfileService profileService;
  private final PseudonymService pseudonymServiceMock = Mockito.mock(PseudonymService.class);

  @BeforeEach
  public void setUp() {
    // Build mocked service
    profileService = new ProfileService(pseudonymServiceMock);
  }

  @Test
  void should_apply() {

    // Init data
    DicomObject dicomObject = new DicomObjectImpl();
    DestinationEntity destinationEntity = new DestinationEntity();
    destinationEntity.setDestinationType(DestinationType.dicom);
    ProjectEntity projectEntity = new ProjectEntity();
    byte[] tabByte = new byte[16];
    tabByte[0] = 1;
    projectEntity.setSecret(tabByte);
    destinationEntity.setProjectEntity(projectEntity);
    ProfileEntity profileEntity = new ProfileEntity();
    List<ProfileElementEntity> profileElementEntities = new ArrayList<>();
    ProfileElementEntity profileElementEntityBasic = new ProfileElementEntity();
    profileElementEntityBasic.setCodename("basic.dicom.profile");
    profileElementEntityBasic.setName("nameBasic");
    ProfileElementEntity profileElementEntityCleanPixelData = new ProfileElementEntity();
    profileElementEntityCleanPixelData.setCodename("clean.pixel.data");
    profileElementEntityCleanPixelData.setName("nameCleanPixel");
    profileElementEntityBasic.setPosition(1);
    profileElementEntityCleanPixelData.setPosition(2);
    profileElementEntityBasic.setAction("ReplaceNull");
    profileElementEntityCleanPixelData.setAction("ReplaceNull");

    profileElementEntities.add(profileElementEntityBasic);
    profileElementEntities.add(profileElementEntityCleanPixelData);
    profileEntity.setProfileElementEntities(profileElementEntities);
    profileEntity.setDefaultIssuerOfPatientId("defaultIssuerOfPatientId");
    Set<MaskEntity> maskEntities = new HashSet<>();
    MaskEntity maskEntity = new MaskEntity();
    maskEntities.add(maskEntity);
    maskEntity.setColor("1234567897");
    maskEntity.setStationName("stationName");
    maskEntity.setRectangles(Arrays.asList(new Rectangle()));
    profileEntity.setMaskEntities(maskEntities);
    AttributeEditorContext context = new AttributeEditorContext(null);
    dicomObject.setString(Tag.PatientID, VR.SH, "patientID");
    dicomObject.setString(Tag.SeriesInstanceUID, VR.SH, "seriesInstanceUID");
    dicomObject.setString(Tag.SOPInstanceUID, VR.SH, "sopInstanceUID");
    dicomObject.setString(Tag.IssuerOfPatientID, VR.SH, "issuerOfPatientID");
    dicomObject.setString(Tag.PixelData, VR.SH, "pixelData");
    dicomObject.setString(Tag.SOPClassUID, VR.SH, "sopClassUID");
    dicomObject.setString(Tag.BurnedInAnnotation, VR.SH, "YES");
    dicomObject.setString(Tag.StationName, VR.SH, "stationName");

    // Mock
    Mockito.when(
            pseudonymServiceMock.generatePseudonym(
                Mockito.any(DestinationEntity.class),
                Mockito.any(DicomObject.class),
                Mockito.anyString()))
        .thenReturn("pseudonym");

    // Call service
    profileService.init(profileEntity);
    profileService.apply(dicomObject, destinationEntity, profileEntity, context);

    // Test results
    Assert.assertNotNull(context.getMaskArea().getColor());
    Assert.assertEquals(1, context.getMaskArea().getShapeList().size());
  }
}
