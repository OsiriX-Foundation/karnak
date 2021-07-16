/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.enums;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class ProfileItemTypeTest {

  @Test
  void should_retrieve_code_meaning() {

    // Call enum
    // Test results
    Assert.assertEquals(
        "Basic Application Confidentiality Profile",
        ProfileItemType.getCodeMeaning("basic.dicom.profile"));
    Assert.assertEquals(
        "Clean Pixel Data Option", ProfileItemType.getCodeMeaning("clean.pixel.data"));
    Assert.assertEquals(null, ProfileItemType.getCodeMeaning("replace.uid"));
    Assert.assertEquals(null, ProfileItemType.getCodeMeaning("action.on.specific.tags"));
    Assert.assertEquals(
        "Retain Safe Private Option", ProfileItemType.getCodeMeaning("action.on.privatetags"));
    Assert.assertEquals(
        "Retain Longitudinal Temporal Information Modified Dates Option",
        ProfileItemType.getCodeMeaning("action.on.dates"));
    Assert.assertEquals(null, ProfileItemType.getCodeMeaning("expression.on.tags"));
  }

  @Test
  void should_retrieve_code_value() {

    // Call enum
    // Test results
    Assert.assertEquals("113100", ProfileItemType.getCodeValue("basic.dicom.profile"));
    Assert.assertEquals("113101", ProfileItemType.getCodeValue("clean.pixel.data"));
    Assert.assertEquals(null, ProfileItemType.getCodeValue("replace.uid"));
    Assert.assertEquals(null, ProfileItemType.getCodeValue("action.on.specific.tags"));
    Assert.assertEquals("113111", ProfileItemType.getCodeValue("action.on.privatetags"));
    Assert.assertEquals("113107", ProfileItemType.getCodeValue("action.on.dates"));
    Assert.assertEquals(null, ProfileItemType.getCodeValue("expression.on.tags"));
  }

  @Test
  void when_code_value_alias_not_found_should_return_null() {
    // Call enum
    // Test results
    Assert.assertEquals(null, ProfileItemType.getCodeValue("not.found"));
  }

  @Test
  void when_code_meaning_alias_not_found_should_return_null() {
    // Call enum
    // Test results
    Assert.assertEquals(null, ProfileItemType.getCodeMeaning("not.found"));
  }
}
