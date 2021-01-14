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
