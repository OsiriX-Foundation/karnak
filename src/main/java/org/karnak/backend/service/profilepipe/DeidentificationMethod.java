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

import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.model.profilepipe.HMAC;

public class DeidentificationMethod {

  private static int generateCodeValue(ProfileElementEntity profileElementEntity) {
    final byte[] key = {1};
    HMAC hmac = new HMAC(key);
    return hmac.intHash(profileElementEntity.getCodename());
  }

  public static Attributes setDefaultDeidentTagValue(
      Attributes dcm,
      String patientID,
      String patientName,
      ProjectEntity projectEntity,
      String pseudonym,
      HMAC hmac) {
    final ProfileEntity profileEntity = projectEntity.getProfileEntity();

    dcm.setString(Tag.PatientID, VR.LO, patientID);
    dcm.setString(Tag.PatientName, VR.PN, patientName);

    dcm.setString(Tag.PatientIdentityRemoved, VR.CS, "YES");

    // DeidentificationMethodCodeSequence
    final Set<ProfileElementEntity> profileElementEntities =
        profileEntity.getProfileElementEntities();
    Sequence deidentificationMethodSequence =
        dcm.newSequence(Tag.DeidentificationMethodCodeSequence, profileElementEntities.size());
    profileElementEntities.forEach(
        profileElementEntity -> {
          final Attributes attributes = new Attributes();
          attributes.setString(Tag.CodeValue, VR.SH, "1234");
          attributes.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
          attributes.setString(Tag.CodeMeaning, VR.LO, profileElementEntity.getCodename());
          deidentificationMethodSequence.add(attributes);
        });

    return dcm;
  }
}
