package org.karnak.ui.extid;

import java.time.LocalDate;
import java.time.LocalTime;

public class Patient {

    private String extid;
    private String patientId;
    private String patientName;
    private LocalDate patientBirthDate;
    private String patientSex;
    private String issuerOfPatientId;

    public String getExtid() {
        return extid;
    }

    public void setExtid(String extid) {
        this.extid = extid;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDate getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientBirthDate(LocalDate patientBirthDate) {
        this.patientBirthDate = patientBirthDate;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getIssuerOfPatientId() {
        return issuerOfPatientId;
    }

    public void setIssuerOfPatientId(String issuerOfPatientId) {
        this.issuerOfPatientId = issuerOfPatientId;
    }

    public Patient(String extid, String patientId, String patientName, LocalDate patientBirthDate, String patientSex, String issuerOfPatientId)
    {
        this.extid = extid;
        this.patientId = patientId;
        this.patientName =patientName;
        this.patientBirthDate= patientBirthDate;
        this.patientSex = patientSex;
        this.issuerOfPatientId = issuerOfPatientId;
    }

}
