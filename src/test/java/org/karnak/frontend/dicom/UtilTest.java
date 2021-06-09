/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.dicom;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.model.dicom.WadoNode;
import org.weasis.dicom.param.DicomNode;

class UtilTest {

  @Test
  void when_network_response_format_xml_host_not_reachable_should_add_correct_tags() {

    // Init data
    DicomNode dicomNode = new DicomNode("fwdAeTitle", 1111);
    StringBuilder result = new StringBuilder();

    // Call method
    Util.getNetworkResponse(
        result, dicomNode.getAet(), dicomNode.getHostname(), dicomNode.getPort(), true, "XML");

    // Test results
    Assert.assertNotNull(result);
    Assert.assertTrue(result.toString().contains("<DcmNetworkStatus>"));
    Assert.assertTrue(result.toString().contains("</DcmNetworkStatus>"));
  }

  @Test
  void when_wado_response_format_xml_host_not_reachable_should_add_correct_tags()
      throws MalformedURLException {

    // Init data
    WadoNode wadoNode = new WadoNode("fwdAeTitle", new URL("http://test.com"));
    StringBuilder result = new StringBuilder();

    // Call method
    Util.getWadoResponse(result, wadoNode, true, "XML");

    // Test results
    Assert.assertNotNull(result);
    Assert.assertTrue(result.toString().contains("<WadoStatus"));
    Assert.assertTrue(result.toString().contains("</WadoStatus>"));
  }
}
