package org.karnak.backend.util;

import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.model.profilepipe.PatientMetadata;

public class PatientClientUtil {
    private PatientClientUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getPseudonym(PatientMetadata patientMetadata, PatientClient cache) {
        if (cache != null) {
            final String key = generateKey(patientMetadata);
            final PseudonymPatient patient = cache.get(key);
            if (patient != null && patientMetadata.compareCachedPatient(patient)) {
                return patient.getPseudonym();
            }
        }
        return null;
    }

    public static String generateKey(String patientID, String patientName, String issuerOfPatientID) {
        return patientID.concat(patientName).concat(issuerOfPatientID);
    }

    public static String generateKey(PseudonymPatient patient) {
        String patientID = patient.getPatientId();
        String patientName = patient.getPatientName();
        String issuerOfPatientID = patient.getIssuerOfPatientId();
        return generateKey(patientID, patientName, issuerOfPatientID);
    }

    public static String generateKey(PatientMetadata patientMetadata) {
        String patientID = patientMetadata.getPatientID();
        String patientName = patientMetadata.getPatientName();
        String issuerOfPatientID = patientMetadata.getIssuerOfPatientID();
        return generateKey(patientID, patientName, issuerOfPatientID);
    }
}
