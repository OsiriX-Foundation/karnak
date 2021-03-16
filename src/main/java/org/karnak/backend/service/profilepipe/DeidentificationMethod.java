/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.dicom.DateTimeUtils;
import org.karnak.backend.enums.ProfileItemType;
import org.karnak.backend.model.profilepipe.HMAC;

public class DeidentificationMethod {

  private static int generateCodeValue(ProfileElementEntity profileElementEntity) {
    final byte[] key = {1};
    HMAC hmac = new HMAC(key);
    return hmac.intHash(profileElementEntity.getCodename());
  }

  public static void setDefaultDeidentTagValue(
      Attributes dcm,
      String patientID,
      String patientName,
      ProjectEntity projectEntity,
      String pseudonym) {
    final ProfileEntity profileEntity = projectEntity.getProfileEntity();

    dcm.setString(Tag.PatientID, VR.LO, patientID);
    dcm.setString(Tag.PatientName, VR.PN, patientName);

    dcm.setString(Tag.PatientIdentityRemoved, VR.CS, "YES");

    // DeidentificationMethodCodeSequence
    dcm.setString(Tag.DeidentificationMethod, VR.LO, projectEntity.getName());
    final Set<ProfileElementEntity> profileElementEntities =
        profileEntity.getProfileElementEntities();
    Sequence deidentificationMethodSequence =
        dcm.newSequence(Tag.DeidentificationMethodCodeSequence, profileElementEntities.size());
    profileElementEntities.forEach(
        profileElementEntity -> {
          final Attributes attributes = new Attributes();
          final String codeValue = ProfileItemType.getCodeValue(profileElementEntity.getCodename());
          final String codeMeaning =
              ProfileItemType.getCodeMeaning(profileElementEntity.getCodename());
          if (codeValue != null) {
            attributes.setString(Tag.CodeValue, VR.SH, codeValue);
            attributes.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
            attributes.setString(Tag.CodeMeaning, VR.LO, codeMeaning);
            deidentificationMethodSequence.add(attributes);
          }
        });

    final String allCodeNames = getAllCodeNames(profileEntity);
    // 0012,0063 -> module patient
    // A description or label of the mechanism or method use to remove the Patient's identity
    dcm.setString(Tag.ClinicalTrialSponsorName, VR.LO, allCodeNames);
    dcm.setString(Tag.ClinicalTrialProtocolID, VR.LO, profileEntity.getName());
    dcm.setString(Tag.ClinicalTrialSubjectID, VR.LO, pseudonym);
    dcm.setNull(Tag.ClinicalTrialProtocolName, VR.LO);
    dcm.setNull(Tag.ClinicalTrialSiteID, VR.LO);
    dcm.setNull(Tag.ClinicalTrialSiteName, VR.LO);

    final LocalDateTime now = LocalDateTime.now();
    dcm.setString(Tag.InstanceCreationDate, VR.DA, DateTimeUtils.formatDA(now));
    dcm.setString(Tag.InstanceCreationTime, VR.TM, DateTimeUtils.formatTM(now));
  }

  public static String getAllCodeNames(ProfileEntity profileEntity) {
    return profileEntity.getProfileElementEntities().stream()
        .map(ProfileElementEntity::getCodename)
        .collect(Collectors.joining("-"));
  }
}
