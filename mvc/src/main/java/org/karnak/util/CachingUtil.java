package org.karnak.util;

import org.karnak.profilepipe.utils.PatientMetadata;
import org.karnak.ui.extid.Patient;

import javax.cache.Cache;
import java.util.Iterator;

public class CachingUtil {
    public CachingUtil() {
    }

    public static String getPseudonym(PatientMetadata patientMetadata, Cache<String, Patient> cache) {
        if (cache != null){
            for(Iterator<Cache.Entry<String, Patient>> cacheElem = cache.iterator(); cacheElem.hasNext();){
                final Cache.Entry<String, Patient> cacheEntry = cacheElem.next();
                final Patient patient= cacheEntry.getValue();
                final String key = cacheEntry.getKey();
                if (patientMetadata.compareCachedPatient(patient)) {
                    return patient.getExtid();
                }
            }
        }
        return null;
    }
}
