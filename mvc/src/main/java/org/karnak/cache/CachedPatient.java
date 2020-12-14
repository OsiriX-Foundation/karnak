package org.karnak.cache;

public class CachedPatient extends Patient {

    public CachedPatient(String pseudonym, String patientId, String patientName, String issuerOfPatientId) {
        super(pseudonym, patientId, patientName, null, null, issuerOfPatientId);
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        updatePatientName(patientName);
    }

    public void setIssuerOfPatientId(String issuerOfPatientId) {
        this.issuerOfPatientId = issuerOfPatientId;
    }
}
