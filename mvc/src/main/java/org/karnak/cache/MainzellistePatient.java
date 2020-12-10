package org.karnak.cache;

import org.dcm4che6.util.DateTimeUtils;

import java.io.Serializable;
import java.time.LocalDate;

public class MainzellistePatient implements PseudonymPatient {
    private String pseudonym;
    private String patientId;
    private String patientFirstName;
    private String patientLastName;
    private LocalDate patientBirthDate;
    private String patientSex;
    private String issuerOfPatientId;

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
        return patientFirstName == null ? patientLastName : String.format("%s^%s", patientLastName, patientFirstName);
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

    @Override
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

    @Override
    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    @Override
    public String getIssuerOfPatientId() {
        return issuerOfPatientId;
    }

    public void setIssuerOfPatientId(String issuerOfPatientId) {
        this.issuerOfPatientId = issuerOfPatientId;
    }

    public MainzellistePatient(String pseudonym, String patientId, String patientFirstName, String patientLastName, LocalDate patientBirthDate, String patientSex, String issuerOfPatientId)
    {
        this.pseudonym = pseudonym;
        this.patientId = patientId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientBirthDate= patientBirthDate;
        this.patientSex = patientSex;
        this.issuerOfPatientId = issuerOfPatientId;
    }

}
