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

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Patient implements PseudonymPatient, Serializable {

	private static final Character SPLIT_CHAR_PATIENT_NAME = '^';

	protected String pseudonym;

	protected String patientId;

	protected String patientName;

	protected String patientFirstName;

	protected String patientLastName;

	protected LocalDate patientBirthDate;

	protected String patientSex;

	protected String issuerOfPatientId;

	protected Patient(String pseudonym, String patientId, String patientName, LocalDate patientBirthDate,
			String patientSex, String issuerOfPatientId) {
		this.pseudonym = pseudonym;
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientFirstName = createPatientFirstName(patientName);
		this.patientLastName = createPatientLastName(patientName);
		this.patientBirthDate = patientBirthDate;
		this.patientSex = patientSex;
		this.issuerOfPatientId = issuerOfPatientId;
	}

	protected Patient(String pseudonym, String patientId, String patientFirstName, String patientLastName,
			LocalDate patientBirthDate, String patientSex, String issuerOfPatientId) {
		this.pseudonym = pseudonym;
		this.patientId = patientId;
		this.patientName = createPatientName(patientFirstName, patientLastName);
		this.patientFirstName = emptyStringIfNull(patientFirstName);
		this.patientLastName = emptyStringIfNull(patientLastName);
		this.patientBirthDate = patientBirthDate;
		this.patientSex = patientSex;
		this.issuerOfPatientId = issuerOfPatientId;
	}

	protected static String createPatientLastName(String patientName) {
		return patientName.split(String.format("\\%c", SPLIT_CHAR_PATIENT_NAME))[0];
	}

	protected static String createPatientFirstName(String patientName) {
		String[] patientNameSplitted = patientName.split(String.format("\\%c", SPLIT_CHAR_PATIENT_NAME));
		if (patientNameSplitted.length > 1) {
			return patientNameSplitted[1];
		}
		return "";
	}

	protected static String createPatientName(String patientFirstName, String patientLastName) {
		if (patientFirstName == null || patientFirstName.equals("")) {
			return patientLastName;
		}
		return String.format("%s%c%s", patientLastName == null ? "" : patientLastName, SPLIT_CHAR_PATIENT_NAME,
				patientFirstName);
	}

	private static String emptyStringIfNull(String value) {
		return value == null ? "" : value;
	}

	public void updatePatientName(String patientName) {
		this.patientName = patientName;
		this.patientFirstName = createPatientFirstName(patientName);
		this.patientLastName = createPatientLastName(patientName);
	}

	protected void updatePatientLastName(String patientLastName) {
		this.patientLastName = emptyStringIfNull(patientLastName);
		this.patientName = createPatientName(patientFirstName, patientLastName);
	}

	protected void updatePatientFirstName(String patientFirstName) {
		this.patientFirstName = emptyStringIfNull(patientFirstName);
		this.patientName = createPatientName(patientFirstName, patientLastName);
	}

	@Override
	public String getPseudonym() {
		return pseudonym;
	}

	@Override
	public String getPatientId() {
		return patientId;
	}

	@Override
	public String getPatientName() {
		return patientName;
	}

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	@Override
	public LocalDate getPatientBirthDate() {
		return patientBirthDate;
	}

	@Override
	public String getPatientSex() {
		return patientSex;
	}

	@Override
	public String getIssuerOfPatientId() {
		return issuerOfPatientId;
	}

}
