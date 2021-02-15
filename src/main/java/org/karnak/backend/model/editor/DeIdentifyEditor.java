/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.editor;

import org.dcm4che6.data.DicomObject;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

public class DeIdentifyEditor implements AttributeEditor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeIdentifyEditor.class);

  private final Profile profile;
  private DestinationEntity destinationEntity;
  private ProfileEntity profileEntity;

  public DeIdentifyEditor(DestinationEntity destinationEntity) {
    this.destinationEntity = destinationEntity;
    this.profileEntity = destinationEntity.getProjectEntity().getProfileEntity();
    this.profile = new Profile(profileEntity);
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    profile.apply(dcm, destinationEntity, profileEntity, context);
  }
}
