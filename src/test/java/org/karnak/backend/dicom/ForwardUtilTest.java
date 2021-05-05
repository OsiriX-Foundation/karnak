/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.karnak.backend.dicom.ForwardUtil.Params;
import org.weasis.dicom.param.DicomNode;

class ForwardUtilTest {

  @Test
  void should_create_params() {

    // Init data
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("test".getBytes());

    // Create params
    Params p = new Params("iuid", "cuid", "tsuid", 0, byteArrayInputStream, null);

    // Test results
    Assert.assertEquals("iuid", p.getIuid());
    Assert.assertEquals("cuid", p.getCuid());
    Assert.assertEquals("tsuid", p.getTsuid());
    Assert.assertEquals(0, p.getPriority());
    Assert.assertEquals(byteArrayInputStream, p.getData());
    Assert.assertNull(p.getAs());
  }

  @Test
  void when_store_no_destination_should_throw_exception() {
    Assertions.assertThrows(
        IllegalStateException.class,
        () -> {
          // Init data
          List<ForwardDestination> destList = new ArrayList<>();
          ForwardDicomNode fwdNode = new ForwardDicomNode("fwdAeTitle");

          // Call method
          ForwardUtil.storeMultipleDestination(fwdNode, destList, null);
        });
  }

  @Test
  void when_store_dicom_dir_should_return() {
    Assertions.assertDoesNotThrow(
        () -> {
          // Init data
          ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("test".getBytes());
          // Create params
          Params p =
              new Params("iuid", "1.2.840.10008.1.3.10", "tsuid", 0, byteArrayInputStream, null);
          List<ForwardDestination> destList = new ArrayList<>();
          ForwardDicomNode forwardDicomNode = new ForwardDicomNode("fwdAeTitle");
          DicomNode dicomNode = new DicomNode("fwdAeTitle", 1111);
          ForwardDestination forwardDestination =
              new DicomForwardDestination(forwardDicomNode, dicomNode);
          destList.add(forwardDestination);

          // Call method
          ForwardUtil.storeMultipleDestination(forwardDicomNode, destList, p);
        });
  }

  @Test
  void when_store_one_dest_remote_dest_invalid_type_dicom_should_throw_io_exception()
      throws IOException {

    Assertions.assertThrows(
        IOException.class,
        () -> {
          // Init data
          ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("test".getBytes());
          // Create params
          Params p = new Params("iuid", "cuid", "tsuid", 0, byteArrayInputStream, null);
          List<ForwardDestination> destList = new ArrayList<>();
          ForwardDicomNode forwardDicomNode = new ForwardDicomNode("fwdAeTitle");
          DicomNode dicomNode = new DicomNode("fwdAeTitle", 1111);
          ForwardDestination forwardDestination =
              new DicomForwardDestination(forwardDicomNode, dicomNode);
          destList.add(forwardDestination);

          // Call method
          ForwardUtil.storeMultipleDestination(forwardDicomNode, destList, p);
        });
  }
}
