package org.karnak.profilepipe.utils;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.api.rqbody.Fields;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.karnak.cache.PseudonymPatient;
import org.weasis.core.util.StringUtil;

public class PatientMetadata {
    private static final String PATIENT_SEX_OTHER = "O";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");

    private final String patientID;
    private final String patientName;
    private final String patientBirthDate;
    private final String issuerOfPatientID;
    private final String patientSex;

    public PatientMetadata(DicomObject dcm, String defaultIsserOfPatientID) {
        patientID = dcm.getString(Tag.PatientID).orElse("");
        patientName = dcm.getString(Tag.PatientName).orElse("");
        patientBirthDate = setPatientBirthDate(dcm.getString(Tag.PatientBirthDate).orElse(""));
        issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse(StringUtil.hasText(defaultIsserOfPatientID) ? defaultIsserOfPatientID : "");
        patientSex = setPatientSex(dcm.getString(Tag.PatientSex).orElse(PATIENT_SEX_OTHER));
    }

    private String setPatientSex(String patientSex) {
        if (!patientSex.equals("M") && !patientSex.equals("F")) {
            return PATIENT_SEX_OTHER;
        }
        return patientSex;
    }

    private String setPatientBirthDate(String rawPatientBirthDate) {
        if (StringUtil.hasText(rawPatientBirthDate)) {
            final LocalDate patientBirthDateLocalDate = DateTimeUtils.parseDA(rawPatientBirthDate);
            return DateTimeUtils.formatDA(patientBirthDateLocalDate);
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

    public String getPatientSex() {
        return patientSex;
    }

    public boolean compareCachedPatient(PseudonymPatient patient) {
        if (patient != null) {
            return (patient.getPatientId().equals(patientID) && patient.getPatientName().equals(patientName) &&
                    DateTimeUtils.formatDA(patient.getPatientBirthDate()).equals(patientBirthDate) &&
                    patient.getIssuerOfPatientId().equals(issuerOfPatientID) &&
                    patient.getPatientSex().equals(patientSex));
        }
        return false;
    }

    public Fields generateMainzellisteFields() {
        return new Fields(patientID, patientName, patientBirthDate, patientSex, issuerOfPatientID);
    }
}
