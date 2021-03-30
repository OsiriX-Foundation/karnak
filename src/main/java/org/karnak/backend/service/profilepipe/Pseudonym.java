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

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.util.TagUtils;
import org.externalid.ExternalIDProvider;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.config.ExternalIDProviderConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
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
  private final List<ExternalIDProvider> externalIDProviderList;

  public Pseudonym() {
    this.externalIdCache = AppConfig.getInstance().getExternalIDCache();
    this.mainzellisteCache = AppConfig.getInstance().getMainzellisteCache();
    this.externalIDProviderList = ExternalIDProviderConfig.getInstance().externalIDProviderList();
  }

  public String generatePseudonym(
      DestinationEntity destinationEntity, Attributes dcm, String defaultIssuerOfPatientID) {

    final PatientMetadata patientMetadata = new PatientMetadata(dcm, defaultIssuerOfPatientID);
    final ExternalIDProviderEntity externalIDProviderEntity =
        destinationEntity.getExternalIDProviderEntity();
    final ExternalIDProviderType externalIDProviderType =
        externalIDProviderEntity.getExternalIDProviderType();

    if (externalIDProviderType.equals(ExternalIDProviderType.EXTID_CACHE)) {
      return getCacheExtid(patientMetadata, destinationEntity.getProjectEntity().getId());
    }

    if (externalIDProviderType.equals(ExternalIDProviderType.EXTID_IN_TAG)) {
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
      return getExternalIDProvider(dcm, "Generate external id with mainzelliste");
    }

    if (destinationEntity.getPseudonymType().equals(PseudonymType.MAINZELLISTE_EXTID)) {
      return getExternalIDProvider(dcm, "Get external id with mainzelliste");
    }

    return null;
  }

  private String getPseudonymInDicom(
      Attributes dcm, DestinationEntity destinationEntity, PatientMetadata patientMetadata) {
    final String cleanTag = destinationEntity.getTag().replaceAll("[(),]", "").toUpperCase();
    final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag));
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

  public String getCacheExtid(PatientMetadata patientMetadata, Long projectID) {
    final String pseudonymCacheExtID =
        PatientClientUtil.getPseudonym(patientMetadata, externalIdCache, projectID);
    if (pseudonymCacheExtID == null) {
      throw new IllegalStateException("Cannot get an external pseudonym in cache");
    }
    return pseudonymCacheExtID;
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

  public String getExternalIDProvider(Attributes dcm, String extertnalIDType) {
    ExternalIDProvider implementation =
        externalIDProviderList.stream()
            .filter(
                externalIDProvider1 ->
                    externalIDProvider1.getExternalIDType().equals(extertnalIDType))
            .findAny()
            .orElse(null);
    if (implementation != null) {
      return implementation.getExternalID(dcm);
    }
    return null;
  }
}
