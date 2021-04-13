/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.echo;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DestinationEchoTest {

  @Test
  void when_same_values_should_be_equal() {
    // Init data
    DestinationEcho destinationEcho = new DestinationEcho("aet", "url", 111);
    DestinationEcho destinationEchoToCompare = new DestinationEcho();
    destinationEchoToCompare.setAet("aet");
    destinationEchoToCompare.setStatus(111);
    destinationEchoToCompare.setUrl("url");

    // Test results
    Assert.assertEquals(destinationEcho, destinationEchoToCompare);
    Assert.assertEquals(destinationEcho.hashCode(), destinationEchoToCompare.hashCode());
  }
}
