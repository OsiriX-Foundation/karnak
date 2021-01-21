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
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.profilepipe.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

public class DeidentifyEditor implements AttributeEditor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeidentifyEditor.class);

  private final Profiles profiles;
  private final DestinationEntity destinationEntity;

  public DeidentifyEditor(DestinationEntity destinationEntity) {
    this.destinationEntity = destinationEntity;
    ProjectEntity projectEntity = destinationEntity.getProjectEntity();
    profiles = new Profiles(projectEntity.getProfileEntity());
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    profiles.apply(dcm, destinationEntity, context);
  }
}
