/*
 * Copyright (c) 2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom.web;

public class InvokeImageDisplay {
  // Non IID request parameters
  public static final String SERIES_UID = "seriesUID";
  public static final String OBJECT_UID = "objectUID";

  /* IHE Radiology Technical Framework Supplement – Invoke Image Display (IID) */
  // HTTP Request Parameters – Patient-based
  public static final String REQUEST_TYPE = "requestType";
  public static final String PATIENT_ID = "patientID";
  public static final String PATIENT_NAME = "patientName";
  public static final String PATIENT_BIRTHDATE = "patientBirthDate";
  public static final String LOWER_DATETIME = "lowerDateTime";
  public static final String UPPER_DATETIME = "upperDateTime";
  public static final String MOST_RECENT_RESULTS = "mostRecentResults";
  public static final String MODALITIES_IN_STUDY = "modalitiesInStudy";
  public static final String VIEWER_TYPE = "viewerType";
  public static final String DIAGNOSTIC_QUALITY = "diagnosticQuality";
  public static final String KEY_IMAGES_ONLY = "keyImagesOnly";
  // Additional patient-based parameters (not IID profile)
  public static final String KEYWORDS = "containsInDescription";

  // HTTP Request Parameters – Study-based
  public static final String STUDY_UID = "studyUID";
  public static final String ACCESSION_NUMBER = "accessionNumber";

  // Well-Known Values for Viewer Type Parameter
  public static final String IHE_BIR = "IHE_BIR";
  public static final String PATIENT_LEVEL = "PATIENT";
  public static final String STUDY_LEVEL = "STUDY";

  private InvokeImageDisplay() {}
}
