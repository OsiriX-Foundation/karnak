package org.karnak.backend.cache;

import java.time.LocalDate;

public class MainzellistePatient extends Patient {

    public MainzellistePatient(String pseudonym, String patientId, String patientFirstName, String patientLastName,
                               LocalDate patientBirthDate, String patientSex, String issuerOfPatientId)
    {
        super(pseudonym, patientId, patientFirstName, patientLastName, patientBirthDate, patientSex, issuerOfPatientId);
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
}
