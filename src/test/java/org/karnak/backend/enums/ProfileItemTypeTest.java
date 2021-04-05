/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.enums;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProfileItemTypeTest {

  @Test
  void should_retrieve_code_meaning() {

    // Call enum
    // Test results
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("basic.dicom.profile"),
        "Basic Application Confidentiality Profile");
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("clean.pixel.data"),
        "Clean Pixel Data Option");
    Assert.assertEquals(ProfileItemType.getCodeMeaning("replace.uid"), null);
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("action.on.specific.tags"), null);
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("action.on.privatetags"),
        "Retain Safe Private Option");
    Assert.assertEquals(
        ProfileItemType.getCodeMeaning("action.on.dates"),
        "Retain Longitudinal Temporal Information Modified Dates Option");
    Assert.assertEquals(ProfileItemType.getCodeMeaning("expression.on.tags"), null);
  }

  @Test
  void should_retrieve_code_value() {

    // Call enum
    // Test results
    Assert.assertEquals(ProfileItemType.getCodeValue("basic.dicom.profile"), "113100");
    Assert.assertEquals(ProfileItemType.getCodeValue("clean.pixel.data"), "113101");
    Assert.assertEquals(ProfileItemType.getCodeValue("replace.uid"), null);
    Assert.assertEquals(ProfileItemType.getCodeValue("action.on.specific.tags"), null);
    Assert.assertEquals(ProfileItemType.getCodeValue("action.on.privatetags"), "113111");
    Assert.assertEquals(ProfileItemType.getCodeValue("action.on.dates"), "113107");
    Assert.assertEquals(ProfileItemType.getCodeValue("expression.on.tags"), null);
  }
}
