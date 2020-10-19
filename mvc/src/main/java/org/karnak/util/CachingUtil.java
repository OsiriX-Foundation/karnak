package org.karnak.util;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.DateTimeUtils;
import org.karnak.ui.extid.Patient;

import javax.cache.Cache;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class CachingUtil {
    public CachingUtil() {
    }

    public static String getPseudonym(DicomObject dcm, Cache<String, Patient> cache) {
        if (cache != null){
            final String patientID = dcm.getString(Tag.PatientID).orElse(null);
            final String patientName = dcm.getString(Tag.PatientName).orElse(null);
            final String rawPatientBirthDate = dcm.getString(Tag.PatientBirthDate).orElse(null);
            String patientBirthDate = null;
            if (rawPatientBirthDate != null) {
                final LocalDate patientBirthDateLocalDate = DateTimeUtils.parseDA(rawPatientBirthDate);
                patientBirthDate = patientBirthDateLocalDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));
            }
            final String issuerOfPatientID = dcm.getString(Tag.IssuerOfPatientID).orElse("");
            String patientSex = dcm.getString(Tag.PatientSex).orElse(null);
            if (!patientSex.equals("M") && !patientSex.equals("F") && !patientSex.equals("O")) {
                patientSex = "O";
            }

            for(Iterator<Cache.Entry<String, Patient>> cacheElem = cache.iterator(); cacheElem.hasNext();){
                final Cache.Entry<String, Patient> cacheEntry = cacheElem.next();
                final Patient patient= cacheEntry.getValue();
                final String key = cacheEntry.getKey();
                final String patientBirthDateFormat = patient.getPatientBirthDate().format(DateTimeFormatter.ofPattern("YYYYMMdd"));
                if (patient.getPatientId().equals(patientID) && patient.getPatientNameDicomFormat().equals(patientName) &&
                        patientBirthDateFormat.equals(patientBirthDate) &&
                        patient.getIssuerOfPatientId().equals(issuerOfPatientID) &&
                        patient.getPatientSex().equals(patientSex)) {
                    return patient.getExtid();
                }
            }
        }
        return null;
    }
}
