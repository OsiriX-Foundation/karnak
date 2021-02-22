/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.PseudonymType;
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

  public String generatePseudonym(
      DestinationEntity destinationEntity, DicomObject dcm, String defaultIssuerOfPatientID) {

    final PatientMetadata patientMetadata = new PatientMetadata(dcm, defaultIssuerOfPatientID);

    if (destinationEntity.getPseudonymType().equals(PseudonymType.CACHE_EXTID)) {
      return getCacheExtid(patientMetadata);
    }

    if (destinationEntity.getPseudonymType().equals(PseudonymType.EXTID_IN_TAG)) {
      return getPseudonymInDicom(dcm, destinationEntity, patientMetadata);
    }

    final String cachedMainezllistePseudonym =
        PatientClientUtil.getPseudonym(patientMetadata, mainzellisteCache);
    if (cachedMainezllistePseudonym != null) {
      cachingMainzellistePseudonym(cachedMainezllistePseudonym, patientMetadata);
      return cachedMainezllistePseudonym;
    }

    if (destinationEntity
        .getPseudonymType()
        .equals(PseudonymType.MAINZELLISTE_PID)) { // MAINZELLISTE
      return getMainzellistePID(patientMetadata);
    }

    if (destinationEntity.getPseudonymType().equals(PseudonymType.MAINZELLISTE_EXTID)) {
      return getMainzellisteExtID(patientMetadata);
    }

    return null;
  }

  private String getPseudonymInDicom(
      DicomObject dcm, DestinationEntity destinationEntity, PatientMetadata patientMetadata) {
    final String cleanTag = destinationEntity.getTag().replaceAll("[(),]", "").toUpperCase();
    final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag)).orElse(null);
    String pseudonymExtidInTag = null;

    if (tagValue != null
        && destinationEntity.getDelimiter() != null
        && destinationEntity.getPosition() != null
        && !destinationEntity.getDelimiter().equals("")) {
      String delimiterSpec =
          SpecialCharacter.escapeSpecialRegexChars(destinationEntity.getDelimiter());
      try {
        pseudonymExtidInTag = tagValue.split(delimiterSpec)[destinationEntity.getPosition()];
      } catch (ArrayIndexOutOfBoundsException e) {
        LOGGER.error("Can not split the external pseudonym", e);
      }
    } else {
      pseudonymExtidInTag = tagValue;
    }

    if (pseudonymExtidInTag == null) {
      throw new IllegalStateException("Cannot get a pseudonym in a DICOM tag");
    } else {
      if (destinationEntity.getSavePseudonym().booleanValue()) {
        final PseudonymApi pseudonymApi = new PseudonymApi();
        pseudonymApi.addExtID(patientMetadata.generateMainzellisteFields(), pseudonymExtidInTag);
      }
    }
    return pseudonymExtidInTag;
  }

  public String getCacheExtid(PatientMetadata patientMetadata) {
    final String pseudonymCacheExtID =
        PatientClientUtil.getPseudonym(patientMetadata, externalIdCache);
    if (pseudonymCacheExtID == null) {
      throw new IllegalStateException("Cannot get an external pseudonym in cache");
    }
    return pseudonymCacheExtID;
  }

  public String getMainzellistePID(PatientMetadata patientMetadata) {
    final PseudonymApi pseudonymApi = new PseudonymApi();
    final String pseudonymMainzellistePID =
        pseudonymApi.generatePID(patientMetadata.generateMainzellisteFields());
    if (pseudonymMainzellistePID == null) {
      throw new IllegalStateException("Cannot get pseudonym of type pid in Mainzelliste API");
    }
    cachingMainzellistePseudonym(pseudonymMainzellistePID, patientMetadata);
    return pseudonymMainzellistePID;
  }

  public String getMainzellisteExtID(PatientMetadata patientMetadata) {
    final PseudonymApi pseudonymApi = new PseudonymApi();
    final String pseudonymMainzellisteExtID =
        pseudonymApi.getExistingExtID(patientMetadata.generateMainzellisteFields());
    if (pseudonymMainzellisteExtID == null) {
      throw new IllegalStateException("Cannot get pseudonym of type extid in Mainzelliste API");
    }
    cachingMainzellistePseudonym(pseudonymMainzellisteExtID, patientMetadata);
    return pseudonymMainzellisteExtID;
  }

  private void cachingMainzellistePseudonym(String pseudonym, PatientMetadata patientMetadata) {
    final MainzellistePatient mainzellistePatient =
        new MainzellistePatient(
            pseudonym,
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
