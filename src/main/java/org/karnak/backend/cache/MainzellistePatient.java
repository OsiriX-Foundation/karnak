/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import java.time.LocalDate;

public class MainzellistePatient extends Patient {

  public MainzellistePatient(
      String pseudonym,
      String patientId,
      String patientFirstName,
      String patientLastName,
      LocalDate patientBirthDate,
      String patientSex,
      String issuerOfPatientId) {
    super(
        pseudonym,
        patientId,
        patientFirstName,
        patientLastName,
        patientBirthDate,
        patientSex,
        issuerOfPatientId);
  }

  public void setPseudonym(String pseudonym) {
    this.pseudonym = pseudonym;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public void setPatientFirstName(String patientFirstName) {
    updatePatientFirstName(patientFirstName);
  }

  public void setPatientLastName(String patientLastName) {
    updatePatientLastName(patientLastName);
  }

  public void setPatientBirthDate(LocalDate patientBirthDate) {
    this.patientBirthDate = patientBirthDate;
  }

  public void setPatientSex(String patientSex) {
    this.patientSex = patientSex;
  }

  public void setIssuerOfPatientId(String issuerOfPatientId) {
    this.issuerOfPatientId = issuerOfPatientId;
  }

  public String toStringUI() {
    return "patientId:'" + patientId + '\'' +
        " patientName:'" + patientName + '\'' +
        " patientFirstName:'" + patientFirstName + '\'' +
        " patientLastName:'" + patientLastName + '\'' +
        " patientBirthDate:" + patientBirthDate +
        " patientSex:'" + patientSex + '\'' +
        " issuerOfPatientId:'" + issuerOfPatientId + '\'';
  }
}
