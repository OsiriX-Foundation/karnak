/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientClientUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(PatientClientUtil.class);

  private PatientClientUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String getPseudonym(PatientMetadata patientMetadata, PatientClient cache) {
    if (cache != null) {
      final String key = generateKey(patientMetadata);
      return getCachedKey(key, patientMetadata, cache);
    }
    return null;
  }

  public static String getPseudonym(
      PatientMetadata patientMetadata, PatientClient cache, Long projectID) {
    if (cache != null) {
      final String key = generateKey(patientMetadata, projectID);
      LOGGER.info("getPseudonym, key generated:" + key);
      return getCachedKey(key, patientMetadata, cache);
    } else {
      LOGGER.info("getPseudonym, cache null");
    }
    return null;
  }

  private static String getCachedKey(
      String key, PatientMetadata patientMetadata, PatientClient cache) {
    final PseudonymPatient patient = cache.get(key);
    if (patient != null && patientMetadata.compareCachedPatient(patient)) {
      return patient.getPseudonym();
    }
    return null;
  }

  public static String generateKey(String patientID, String issuerOfPatientID) {
    return patientID.concat(issuerOfPatientID == null ? "" : issuerOfPatientID);
  }

  public static String generateKey(PseudonymPatient patient) {
    String patientID = patient.getPatientId();
    String issuerOfPatientID = patient.getIssuerOfPatientId();
    return generateKey(patientID, issuerOfPatientID);
  }

  public static String generateKey(PatientMetadata patientMetadata) {
    String patientID = patientMetadata.getPatientID();
    String issuerOfPatientID = patientMetadata.getIssuerOfPatientID();
    return generateKey(patientID, issuerOfPatientID);
  }

  public static String generateKey(PatientMetadata patientMetadata, Long projectID) {
    final String key = generateKey(patientMetadata);
    return key.concat(projectID == null ? "" : projectID.toString());
  }

  public static String generateKey(PseudonymPatient pseudonymPatient, Long projectID) {
    final String key = generateKey(pseudonymPatient);
    return key.concat(projectID == null ? "" : projectID.toString());
  }
}
