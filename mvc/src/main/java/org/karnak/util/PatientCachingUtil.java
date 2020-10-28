package org.karnak.util;

import org.karnak.profilepipe.utils.PatientMetadata;
import org.karnak.ui.extid.Patient;

import javax.cache.Cache;

public class PatientCachingUtil {
    public PatientCachingUtil() {
    }

    public static String getPseudonym(PatientMetadata patientMetadata, Cache<String, Patient> cache) {
        if (cache != null) {
            final String key = generateKey(patientMetadata);
            final Patient patient = cache.get(key);
            if (patient != null && patientMetadata.compareCachedPatient(patient)) {
                return patient.getExtid();
            }
        }
        return null;
    }

    public static String generateKey(String PatientID, String PatientName, String IssuerOfPatientID) {
        return PatientID.concat(PatientName);
    }

    public static String generateKey(Patient patient) {
        String PatientID = patient.getPatientId();
        String PatientName = patient.getPatientNameDicomFormat();
        String IssuerOfPatientID = patient.getIssuerOfPatientId();
        return generateKey(PatientID, PatientName, IssuerOfPatientID);
    }

    public static String generateKey(PatientMetadata patientMetadata) {
        String PatientID = patientMetadata.getPatientID();
        String PatientName = patientMetadata.getPatientName();
        String IssuerOfPatientID = patientMetadata.getIssuerOfPatientID();
        return generateKey(PatientID, PatientName, IssuerOfPatientID);
    }
}
