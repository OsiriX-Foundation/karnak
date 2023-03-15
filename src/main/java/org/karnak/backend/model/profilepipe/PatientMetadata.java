/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profilepipe;

import java.time.LocalDate;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.cache.Patient;
import org.weasis.dicom.util.DateUtil;

public class PatientMetadata {

	private static final String PATIENT_SEX_OTHER = "O";

	private final String patientID;

	private final String patientName;

	private final String patientBirthDate;

	private final String issuerOfPatientID;

	private final String patientSex;

	public PatientMetadata(Attributes dcm) {
		patientID = dcm.getString(Tag.PatientID, "");
		patientName = dcm.getString(Tag.PatientName, "");
		patientBirthDate = setPatientBirthDate(dcm.getString(Tag.PatientBirthDate));
		issuerOfPatientID = "";
		patientSex = setPatientSex(dcm.getString(Tag.PatientSex, PATIENT_SEX_OTHER));
	}

	public PatientMetadata(Attributes dcm, String issuerOfPatientIDByDefault) {
		patientID = dcm.getString(Tag.PatientID, "");
		patientName = dcm.getString(Tag.PatientName, "");
		patientBirthDate = setPatientBirthDate(dcm.getString(Tag.PatientBirthDate));
		issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID, issuerOfPatientIDByDefault);
		patientSex = setPatientSex(dcm.getString(Tag.PatientSex, PATIENT_SEX_OTHER));
	}

	private String setPatientSex(String patientSex) {
		if (!patientSex.equals("M") && !patientSex.equals("F")) {
			return PATIENT_SEX_OTHER;
		}
		return patientSex;
	}

	private String setPatientBirthDate(String rawPatientBirthDate) {
		return DateUtil.formatDicomDate(DateUtil.getDicomDate(rawPatientBirthDate));
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
		return DateUtil.getDicomDate(patientBirthDate);
	}

	public String getIssuerOfPatientID() {
		return issuerOfPatientID;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public boolean compareCachedPatient(Patient patient) {
		if (patient != null) {
			boolean samePatient = patient.getPatientId().equals(patientID);
			samePatient = samePatient && (patient.getIssuerOfPatientId() == null
					|| patient.getIssuerOfPatientId().equals(issuerOfPatientID));
			return samePatient;
		}
		return false;
	}

	public Fields generateMainzellisteFields() {
		return new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
	}

}
