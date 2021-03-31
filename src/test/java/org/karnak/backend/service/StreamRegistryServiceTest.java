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
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.internal.DicomObjectImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.model.Series;
import org.karnak.backend.model.SopInstance;
import org.karnak.backend.model.Study;
import org.springframework.boot.test.context.SpringBootTest;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.DicomProgress;

@SpringBootTest
class StreamRegistryServiceTest {

  // Service
  private StreamRegistryService streamRegistryService;

  @BeforeEach
  public void setUp() {

    // Build mocked service
    streamRegistryService = new StreamRegistryService();
  }

  @Test
  void should_add_study_serie_sop() {
    // Init data
    DicomObject dcm = new DicomObjectImpl();
    dcm.setString(Tag.StudyInstanceUID, VR.SH, "studyInstanceUID");
    dcm.setString(Tag.SeriesInstanceUID, VR.SH, "seriesInstanceUID");
    dcm.setString(Tag.SOPInstanceUID, VR.SH, "sopInstanceUID");

    AttributeEditorContext context = new AttributeEditorContext(null);

    // Call service
    streamRegistryService.setEnable(true);
    streamRegistryService.apply(dcm, context);

    // Test results
    Assert.assertNotNull(streamRegistryService.getStudy("studyInstanceUID"));
    Assert.assertNotNull(
        streamRegistryService.getStudy("studyInstanceUID").getSeries("seriesInstanceUID"));
    Assert.assertNotNull(
        streamRegistryService
            .getStudy("studyInstanceUID")
            .getSeries("seriesInstanceUID")
            .getSopInstance("sopInstanceUID"));
  }

  @Test
  void should_remove_study() {

    // Call service
    streamRegistryService.addStudy(new Study("studyInstanceUID", "patientId"));

    // Test study added
    Assert.assertNotNull(streamRegistryService.getStudy("studyInstanceUID"));

    // Call service
    streamRegistryService.removeStudy("studyInstanceUID");

    // Test result
    Assert.assertNull(streamRegistryService.getStudy("studyInstanceUID"));
  }

  @Test
  void should_update_sopInstance() {
    // Init data
    DicomProgress dicomProgress = new DicomProgress();
    DicomObject dicomObject = new DicomObjectImpl();
    dicomObject.setString(Tag.AffectedSOPInstanceUID, VR.SH, "affectedSOPInstanceUID");
    dicomProgress.setAttributes(dicomObject);
    Study study = new Study("studyInstanceUID", "patientId");
    Series serie = new Series("seriesInstantUID");
    SopInstance sopInstance = new SopInstance("affectedSOPInstanceUID");
    serie.addSopInstance(sopInstance);
    study.addSeries(serie);

    // Call service
    streamRegistryService.setEnable(true);
    streamRegistryService.addStudy(study);
    streamRegistryService.update(dicomProgress);

    // Test result
    Assert.assertTrue(sopInstance.isSent());
  }
}
