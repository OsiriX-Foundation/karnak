package org.karnak.profilepipe.utils;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.api.rqbody.Fields;
import org.karnak.ui.extid.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientMetadata {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
    private String patientID;
    private String patientName;
    private String patientBirthDate;
    private String issuerOfPatientID;
    private String patientSex;

    private final String PATIENT_SEX_OTHER = "0";

    public PatientMetadata(DicomObject dcm) {
        patientID = dcm.getString(Tag.PatientID).orElse(null);
        patientName = dcm.getString(Tag.PatientName).orElse(null);
        patientBirthDate = setPatientBirthDate(dcm.getString(Tag.PatientBirthDate).orElse(""));
        issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse("");
        patientSex = setPatientSex(dcm.getString(Tag.PatientSex).orElse("O"));
    }

    private String setPatientSex(String patientSex) {
        if (!patientSex.equals("M") && !patientSex.equals("F") && !patientSex.equals("O")) {
            return PATIENT_SEX_OTHER;
        }
        return patientSex;
    }

    private String setPatientBirthDate(String rawPatientBirthDate) {
        if (rawPatientBirthDate != null && !rawPatientBirthDate.equals("")) {
            final LocalDate patientBirthDateLocalDate = DateTimeUtils.parseDA(rawPatientBirthDate);
            return patientBirthDateLocalDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        }
        return "";
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientLastName() {
        return patientName.split("\\^")[0];
    }

    public String getPatientFirstName() {
        String[] patientNameSplitted = patientName.split("\\^");
        if (patientNameSplitted.length > 1) {
            return patientNameSplitted[1];
        }
        return null;
    }

    public String getPatientBirthDate() {
        return patientBirthDate;
    }

    public LocalDate getLocalDatePatientBirthDate() {
        if (patientBirthDate != null && !patientBirthDate.equals("")) {
            return DateTimeUtils.parseDA(patientBirthDate);
        }
        return null;
    }

    public String getIssuerOfPatientID() {
        return issuerOfPatientID;
    }

    public void setIssuerOfPatientID(String issuerOfPatientID) {
        this.issuerOfPatientID = issuerOfPatientID;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public boolean compareCachedPatient(Patient patient) {
        if (patient != null) {
            LocalDate localDatePatientBirthDate = patient.getPatientBirthDate();
            String patientBirthDateFormat = "";
            if (localDatePatientBirthDate != null) {
                patientBirthDateFormat = localDatePatientBirthDate.format(formatter);
            }
            return (patient.getPatientId().equals(patientID) && patient.getPatientNameDicomFormat().equals(patientName) &&
                    patientBirthDateFormat.equals(patientBirthDate) &&
                    patient.getIssuerOfPatientId().equals(issuerOfPatientID) &&
                    patient.getPatientSex().equals(patientSex));
        }
        return false;
    }

    public Fields generateMainzellisteFields() {
        return new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
    }
}
