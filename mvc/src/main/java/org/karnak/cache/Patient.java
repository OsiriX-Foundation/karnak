package org.karnak.cache;

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

    public Patient(String pseudonym, String patientId, String patientName, LocalDate patientBirthDate,
                   String patientSex, String issuerOfPatientId)
    {
        this.pseudonym = pseudonym;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientFirstName = createPatientFirstName(patientName);
        this.patientLastName = createPatientLastName(patientName);
        this.patientBirthDate= patientBirthDate;
        this.patientSex = patientSex;
        this.issuerOfPatientId = issuerOfPatientId;
    }

    public Patient(String pseudonym, String patientId, String patientFirstName, String patientLastName,
                   LocalDate patientBirthDate, String patientSex, String issuerOfPatientId)
    {
        this.pseudonym = pseudonym;
        this.patientId = patientId;
        this.patientName = createPatientName(patientFirstName, patientLastName);
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientBirthDate= patientBirthDate;
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
        return String.format("%s%c%s", patientLastName == null ? "" : patientLastName, SPLIT_CHAR_PATIENT_NAME, patientFirstName);
    }

    protected void updatePatientName(String patientName) {
        this.patientName = patientName;
        this.patientFirstName = createPatientFirstName(patientName);
        this.patientLastName = createPatientLastName(patientName);
    }

    protected void updatePatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
        this.patientName = createPatientName(patientFirstName, patientLastName);
    }

    protected void updatePatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
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
