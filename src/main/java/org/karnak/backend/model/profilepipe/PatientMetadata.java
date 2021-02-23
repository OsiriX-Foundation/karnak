/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profilepipe;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.cache.PseudonymPatient;
import org.weasis.core.util.StringUtil;

public class PatientMetadata {

  private static final String PATIENT_SEX_OTHER = "O";

  private final String patientID;
  private final String patientName;
  private final String patientBirthDate;
  private final String issuerOfPatientID;
  private final String patientSex;

  public PatientMetadata(DicomObject dcm, String defaultIsserOfPatientID) {
    patientID = dcm.getString(Tag.PatientID).orElse("");
    patientName = dcm.getString(Tag.PatientName).orElse("");
    patientBirthDate = setPatientBirthDate(dcm.getString(Tag.PatientBirthDate).orElse(""));
    issuerOfPatientID =
        dcm.getString(Tag.IssuerOfPatientID)
            .orElse(StringUtil.hasText(defaultIsserOfPatientID) ? defaultIsserOfPatientID : "");
    patientSex = setPatientSex(dcm.getString(Tag.PatientSex).orElse(PATIENT_SEX_OTHER));
  }

  private String setPatientSex(String patientSex) {
    if (!patientSex.equals("M") && !patientSex.equals("F")) {
      return PATIENT_SEX_OTHER;
    }
    return patientSex;
  }

  private String setPatientBirthDate(String rawPatientBirthDate) {
    if (StringUtil.hasText(rawPatientBirthDate)) {
      try {
        final LocalDate patientBirthDateLocalDate = DateTimeUtils.parseDA(rawPatientBirthDate);
        return DateTimeUtils.formatDA(patientBirthDateLocalDate);
      } catch (DateTimeParseException dateTimeParseException) {
        return "";
      }
    }
    return "";
  }

  public String getPatientID() {
    return patientID;
  }

  public String getPatientName() {
    return patientName;
  }

  public String getPatientLastName() {
    return patientName.split("\\^")[0];
  }

  public String getPatientFirstName() {
    String[] patientNameSplitted = patientName.split("\\^");
    if (patientNameSplitted.length > 1) {
      return patientNameSplitted[1];
    }
    return "";
  }

  public String getPatientBirthDate() {
    return patientBirthDate;
  }

  public LocalDate getLocalDatePatientBirthDate() {
    if (patientBirthDate != null && !patientBirthDate.equals("")) {
      try {
        return DateTimeUtils.parseDA(patientBirthDate);
      } catch (DateTimeParseException dateTimeParseException) {
        return null;
      }
    }
    return null;
  }

  public String getIssuerOfPatientID() {
    return issuerOfPatientID;
  }

  public String getPatientSex() {
    return patientSex;
  }

  public boolean compareCachedPatient(PseudonymPatient patient) {
    if (patient != null) {
      boolean samePatient = patient.getPatientId().equals(patientID);
      samePatient =
          samePatient
              && (patient.getIssuerOfPatientId() == null
                  || patient.getIssuerOfPatientId().equals(issuerOfPatientID));
      return samePatient;
    }
    return false;
  }

  public Fields generateMainzellisteFields() {
    return new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
  }
}
