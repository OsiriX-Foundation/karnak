/*
 * Copyright (c) 2024 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.extid;

// TODO: currently not used but should be used to replace filters in ExternalIDGrid
public class PatientFilter {

	private String extidFilter;

	private String patientIdFilter;

	private String patientFirstNameFilter;

	private String patientLastNameFilter;

	private String issuerOfPatientIDFilter;

	public String getExtidFilter() {
		return extidFilter;
	}

	public void setExtidFilter(String extidFilter) {
		this.extidFilter = extidFilter;
	}

	public String getPatientIdFilter() {
		return patientIdFilter;
	}

	public void setPatientIdFilter(String patientIdFilter) {
		this.patientIdFilter = patientIdFilter;
	}

	public String getPatientFirstNameFilter() {
		return patientFirstNameFilter;
	}

	public void setPatientFirstNameFilter(String patientFirstNameFilter) {
		this.patientFirstNameFilter = patientFirstNameFilter;
	}

	public String getPatientLastNameFilter() {
		return patientLastNameFilter;
	}

	public void setPatientLastNameFilter(String patientLastNameFilter) {
		this.patientLastNameFilter = patientLastNameFilter;
	}

	public String getIssuerOfPatientIDFilter() {
		return issuerOfPatientIDFilter;
	}

	public void setIssuerOfPatientIDFilter(String issuerOfPatientIDFilter) {
		this.issuerOfPatientIDFilter = issuerOfPatientIDFilter;
	}

}
