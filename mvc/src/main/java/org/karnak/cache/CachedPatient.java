package org.karnak.cache;

public class CachedPatient extends Patient {

    public CachedPatient(String pseudonym, String patientId, String patientFirstName, String patientLastName, String issuerOfPatientId) {
        super(pseudonym, patientId, patientFirstName, patientLastName, null, null, issuerOfPatientId);
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

    public void setIssuerOfPatientId(String issuerOfPatientId) {
        this.issuerOfPatientId = issuerOfPatientId;
    }

    @Override
    public String toString() {
        return String.format("External pseudonym: %s, Patient ID: %s, Patient name: %s, Issuer of patient ID: %s",
                pseudonym, patientId, patientName, issuerOfPatientId);
    }
}
