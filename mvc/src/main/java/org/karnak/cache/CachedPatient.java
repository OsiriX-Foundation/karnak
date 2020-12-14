package org.karnak.cache;

import java.io.Serializable;
import java.time.LocalDate;

public class CachedPatient implements PseudonymPatient, Serializable {
    private String pseudonym;
    private String patientId;
    private String patientName;
    private String issuerOfPatientId;

    public CachedPatient(String pseudonym, String patientId, String patientName, String issuerOfPatientId) {
        this.pseudonym = pseudonym;
        this.patientId = patientId;
        this.patientName = patientName;
        this.issuerOfPatientId = issuerOfPatientId;
    }

    @Override
    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    @Override
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String getIssuerOfPatientId() {
        return issuerOfPatientId;
    }

    public void setIssuerOfPatientId(String issuerOfPatientId) {
        this.issuerOfPatientId = issuerOfPatientId;
    }

    @Override
    public LocalDate getPatientBirthDate() {
        return null;
    }

    @Override
    public String getPatientSex() {
        return null;
    }
}
