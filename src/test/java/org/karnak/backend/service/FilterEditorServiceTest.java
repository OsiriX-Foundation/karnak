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

import java.util.HashSet;
import java.util.Set;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.internal.DicomObjectImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.weasis.dicom.param.AttributeEditorContext;
import org.weasis.dicom.param.AttributeEditorContext.Abort;

@SpringBootTest
class FilterEditorServiceTest {

  // Service
  private FilterEditorService filterEditorService;

  @BeforeEach
  public void setUp() {

    // Build mocked service
    filterEditorService = new FilterEditorService();
  }

  @Test
  void when_no_class_uid_found_should_modify_context() {
    // Init data
    // SopClassUIDEntities
    Set<SOPClassUIDEntity> sopClassUIDEntities = new HashSet<>();
    SOPClassUIDEntity sopClassUIDEntityFirst = new SOPClassUIDEntity();
    SOPClassUIDEntity sopClassUIDEntitySecond = new SOPClassUIDEntity();
    sopClassUIDEntityFirst.setUid("TEST FIRST");
    sopClassUIDEntitySecond.setUid("TEST SECOND");
    sopClassUIDEntities.add(sopClassUIDEntityFirst);
    sopClassUIDEntities.add(sopClassUIDEntitySecond);

    // DicomObject
    DicomObject dcm = new DicomObjectImpl();
    dcm.setString(Tag.SOPClassUID, VR.SH, "TEST NOT FOUND");

    // AttributeEditorContext
    AttributeEditorContext context = new AttributeEditorContext(null);

    // Call service
    filterEditorService.init(sopClassUIDEntities);
    filterEditorService.apply(dcm, context);

    // Test results
    Assert.assertEquals(Abort.FILE_EXCEPTION, context.getAbort());
  }

  @Test
  void when_class_uid_found_should_not_modify_context() {
    // Init data
    // SopClassUIDEntities
    Set<SOPClassUIDEntity> sopClassUIDEntities = new HashSet<>();
    SOPClassUIDEntity sopClassUIDEntityFirst = new SOPClassUIDEntity();
    SOPClassUIDEntity sopClassUIDEntitySecond = new SOPClassUIDEntity();
    sopClassUIDEntityFirst.setUid("TEST FIRST");
    sopClassUIDEntitySecond.setUid("TEST SECOND");
    sopClassUIDEntities.add(sopClassUIDEntityFirst);
    sopClassUIDEntities.add(sopClassUIDEntitySecond);

    // DicomObject
    DicomObject dcm = new DicomObjectImpl();
    dcm.setString(Tag.SOPClassUID, VR.SH, "TEST FIRST");

    // AttributeEditorContext
    AttributeEditorContext context = new AttributeEditorContext(null);

    // Call service
    filterEditorService.init(sopClassUIDEntities);
    filterEditorService.apply(dcm, context);

    // Test results
    Assert.assertNotEquals(Abort.FILE_EXCEPTION, context.getAbort());
  }
}
