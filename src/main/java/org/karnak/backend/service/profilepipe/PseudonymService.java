package org.karnak.backend.service.profilepipe;

import java.io.IOException;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.IdTypes;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;
import org.karnak.backend.util.SpecialCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PseudonymService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PseudonymService.class);

  private final PatientClient externalIdCache;
  private final PatientClient mainzellisteCache;

  @Autowired
  public PseudonymService() {
    this.externalIdCache = AppConfig.getInstance().getExternalIDCache();
    this.mainzellisteCache = AppConfig.getInstance().getMainzellisteCache();
  }

  public String generatePseudonym(DestinationEntity destinationEntity, DicomObject dcm,
      String defaultIsserOfPatientID) {
    String pseudonym;
    if (destinationEntity.getSavePseudonym() != null
        && destinationEntity.getSavePseudonym() == false) {
      pseudonym = getPseudonymInDicom(dcm, destinationEntity);
      if (pseudonym == null) {
        throw new IllegalStateException("Cannot get a pseudonym in a DICOM tag");
      }
      return pseudonym;
    } else if (destinationEntity.getIdTypes().equals(IdTypes.EXTID)) {
      pseudonym = PatientClientUtil
          .getPseudonym(new PatientMetadata(dcm, defaultIsserOfPatientID), externalIdCache);
      if (pseudonym != null) {
        return pseudonym;
      }
    }
        PatientMetadata patientMetadata = new PatientMetadata(dcm, defaultIsserOfPatientID);
    try {
      return getMainzellistePseudonym(patientMetadata, getPseudonymInDicom(dcm,
          destinationEntity),
          destinationEntity.getIdTypes());
    } catch (Exception e) {
      LOGGER.error("Cannot get a pseudonym with Mainzelliste API {}", e);
      throw new IllegalStateException("Cannot get a pseudonym in cache or with Mainzelliste API");
    }
  }

  private String getPseudonymInDicom(DicomObject dcm, DestinationEntity destinationEntity) {
    if (destinationEntity.getIdTypes().equals(IdTypes.ADD_EXTID)) {
      String cleanTag = destinationEntity.getTag().replaceAll("[(),]", "").toUpperCase();
      final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag)).orElse(null);
      if (tagValue != null && destinationEntity.getDelimiter() != null
          && destinationEntity.getPosition() != null) {
        String delimiterSpec = SpecialCharacter
            .escapeSpecialRegexChars(destinationEntity.getDelimiter());
        try {
          return tagValue.split(delimiterSpec)[destinationEntity.getPosition()];
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
