package org.karnak.cache;

import org.karnak.profilepipe.utils.PatientMetadata;

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

    public static String generateKey(String patientID,String issuerOfPatientID) {
        return patientID.concat(issuerOfPatientID);
    }

    public static String generateKey(PseudonymPatient patient) {
        String patientID = patient.getPatientId();
        String issuerOfPatientID = patient.getIssuerOfPatientId();
        return generateKey(patientID, issuerOfPatientID);
    }

    public static String generateKey(PatientMetadata patientMetadata) {
        String patientID = patientMetadata.getPatientID();
        String issuerOfPatientID = patientMetadata.getIssuerOfPatientID();
        return generateKey(patientID, issuerOfPatientID);
    }
}
