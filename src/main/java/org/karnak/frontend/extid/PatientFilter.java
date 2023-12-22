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
