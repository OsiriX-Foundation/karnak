package org.karnak.backend.service.profilepipe;

import java.io.IOException;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.configuration.AppConfig;
import org.karnak.backend.data.entity.Destination;
import org.karnak.backend.enums.IdTypes;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;
import org.karnak.backend.util.SpecialCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pseudonym {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pseudonym.class);

    private final PatientClient externalIdCache;
    private final PatientClient mainzellisteCache;

    public Pseudonym() {
        externalIdCache = AppConfig.getInstance().getExternalIDCache();
        mainzellisteCache = AppConfig.getInstance().getMainzellisteCache();
    }

    public String generatePseudonym(Destination destination, DicomObject dcm,
        String defaultIsserOfPatientID) {
        String pseudonym;
        if (destination.getSavePseudonym() != null && destination.getSavePseudonym() == false) {
            pseudonym = getPseudonymInDicom(dcm, destination);
            if (pseudonym == null) {
                throw new IllegalStateException("Cannot get a pseudonym in a DICOM tag");
            }
            return pseudonym;
        } else if (destination.getIdTypes().equals(IdTypes.EXTID)) {
            pseudonym = PatientClientUtil.getPseudonym(new PatientMetadata(dcm, defaultIsserOfPatientID), externalIdCache);
            if (pseudonym != null) {
                return pseudonym;
            }
        }
        PatientMetadata patientMetadata = new PatientMetadata(dcm, defaultIsserOfPatientID);
        try {
            return getMainzellistePseudonym(patientMetadata, getPseudonymInDicom(dcm, destination),
                    destination.getIdTypes());
        } catch (Exception e) {
            LOGGER.error("Cannot get a pseudonym with Mainzelliste API {}", e);
            throw new IllegalStateException("Cannot get a pseudonym in cache or with Mainzelliste API");
        }
    }

    private String getPseudonymInDicom(DicomObject dcm, Destination destination) {
        if (destination.getIdTypes().equals(IdTypes.ADD_EXTID)) {
            String cleanTag = destination.getTag().replaceAll("[(),]", "").toUpperCase();
            final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag)).orElse(null);
            if (tagValue != null && destination.getDelimiter() != null
                && destination.getPosition() != null) {
                String delimiterSpec = SpecialCharacter
                    .escapeSpecialRegexChars(destination.getDelimiter());
                try {
                    return tagValue.split(delimiterSpec)[destination.getPosition()];
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.error("Can not split the external pseudonym", e);
                    return null;
                }
            } else {
                return tagValue;
            }
        }
        return null;
    }

    public String getMainzellistePseudonym(PatientMetadata patientMetadata, String externalPseudonym, IdTypes idTypes) throws IOException, InterruptedException {
        final String cachedPseudonym = PatientClientUtil.getPseudonym(patientMetadata, mainzellisteCache);
        if (cachedPseudonym != null) {
            cachingMainzellistePseudonym(cachedPseudonym, patientMetadata);
            return cachedPseudonym;
        }

        PseudonymApi pseudonymApi = new PseudonymApi(externalPseudonym);
        final Fields newPatientFields = patientMetadata.generateMainzellisteFields();

        String pseudonym = pseudonymApi.createPatient(newPatientFields, idTypes);
        cachingMainzellistePseudonym(pseudonym, patientMetadata);
        return pseudonym;
    }

    private void cachingMainzellistePseudonym(String pseudonym, PatientMetadata patientMetadata) {
        final MainzellistePatient mainzellistePatient = new MainzellistePatient(pseudonym,
                patientMetadata.getPatientID(),
                patientMetadata.getPatientFirstName(),
                patientMetadata.getPatientLastName(),
                patientMetadata.getLocalDatePatientBirthDate(),
                patientMetadata.getPatientSex(),
                patientMetadata.getIssuerOfPatientID());
        String cacheKey = PatientClientUtil.generateKey(patientMetadata);
        mainzellisteCache.put(cacheKey, mainzellistePatient);
    }
}
