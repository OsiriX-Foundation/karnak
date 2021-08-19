/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping.component;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class MappingInputComponentTest {

  @Test
  void should_create_mapping_input_component() {
    // Call constructor
    MappingInputComponent mappingInputComponent = new MappingInputComponent();

    // Test results
    Assert.assertNotNull(mappingInputComponent);
    Assert.assertNotNull(mappingInputComponent.getFindButton());
    Assert.assertNotNull(mappingInputComponent.getPseudonymTextField());
  }
}
