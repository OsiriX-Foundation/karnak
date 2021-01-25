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
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weasis.dicom.param.AttributeEditor;
import org.weasis.dicom.param.AttributeEditorContext;

@Service
public class DeIdentifyEditorService implements AttributeEditor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeIdentifyEditorService.class);

  private final ProfileService profileService;
  private DestinationEntity destinationEntity;
  private ProfileEntity profileEntity;

  @Autowired
  public DeIdentifyEditorService(final ProfileService profileService) {
    this.profileService = profileService;
  }

  public void init(DestinationEntity destinationEntity) {
    this.destinationEntity = destinationEntity;
    this.profileEntity = destinationEntity.getProjectEntity().getProfileEntity();
    profileService.init(profileEntity);
  }

  @Override
  public void apply(DicomObject dcm, AttributeEditorContext context) {
    profileService.apply(dcm, destinationEntity, profileEntity, context);
  }
}
