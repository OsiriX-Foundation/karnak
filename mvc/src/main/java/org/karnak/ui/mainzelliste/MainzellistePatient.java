package org.karnak.ui.mainzelliste;

import org.dcm4che6.util.DateTimeUtils;

import java.io.Serializable;
import java.time.LocalDate;

public class MainzellistePatient implements Serializable {
    private String extid;
    private String patientId;
    private String patientFirstName;
    private String patientLastName;
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

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public LocalDate getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientBirthDate(LocalDate patientBirthDate) {
        this.patientBirthDate = patientBirthDate;
    }

    public String getFormatPatientBirthDate() {
        if (patientBirthDate != null) {
            return DateTimeUtils.formatDA(patientBirthDate);
        }
        return "";
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

    public String getPatientNameDicomFormat(){
        return patientFirstName == null ? patientLastName : String.format("%s^%s", patientLastName, patientFirstName);
    }

    public MainzellistePatient(String extid, String patientId, String patientFirstName, String patientLastName,
                               LocalDate patientBirthDate, String patientSex, String issuerOfPatientId)
    {
        this.extid = extid;
        this.patientId = patientId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientBirthDate= patientBirthDate;
        this.patientSex = patientSex;
        this.issuerOfPatientId = issuerOfPatientId;
    }
}
