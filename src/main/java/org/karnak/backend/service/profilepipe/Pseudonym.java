/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.profilepipe;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.util.TagUtils;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.PseudonymType;
import org.karnak.backend.model.profilepipe.PatientMetadata;
import org.karnak.backend.util.PatientClientUtil;
import org.karnak.backend.util.SpecialCharacter;

@Slf4j
public class Pseudonym {

	private final PatientClient externalIdCache;

	public Pseudonym() {
		this.externalIdCache = AppConfig.getInstance().getExternalIDCache();
	}

	public String generatePseudonym(DestinationEntity destinationEntity, Attributes dcm) {

		PatientMetadata patientMetadata;
		if (destinationEntity.getIssuerByDefault() == null || destinationEntity.getIssuerByDefault().equals("")) {
			patientMetadata = new PatientMetadata(dcm);
		}
		else {
			patientMetadata = new PatientMetadata(dcm, destinationEntity.getIssuerByDefault());
		}

		if (destinationEntity.getPseudonymType().equals(PseudonymType.CACHE_EXTID)) {
			return getCacheExtid(patientMetadata, destinationEntity.getDeIdentificationProjectEntity().getId());
		}

		if (destinationEntity.getPseudonymType().equals(PseudonymType.EXTID_IN_TAG)) {
			return getPseudonymInDicom(dcm, destinationEntity, patientMetadata);
		}

		// TODO TELIMA-289: pseudo service

		return null;
	}

	private String getPseudonymInDicom(Attributes dcm, DestinationEntity destinationEntity,
			PatientMetadata patientMetadata) {
		final String cleanTag = destinationEntity.getTag().replaceAll("[(),]", "").toUpperCase();
		final String tagValue = dcm.getString(TagUtils.intFromHexString(cleanTag));
		String pseudonymExtidInTag = null;

		if (tagValue != null && destinationEntity.getDelimiter() != null && destinationEntity.getPosition() != null
				&& !destinationEntity.getDelimiter().equals("")) {
			String delimiterSpec = SpecialCharacter.escapeSpecialRegexChars(destinationEntity.getDelimiter());
			try {
				pseudonymExtidInTag = tagValue.split(delimiterSpec)[destinationEntity.getPosition()];
			}
			catch (ArrayIndexOutOfBoundsException e) {
				log.error("Can not split the external pseudonym", e);
			}
		}
		else {
			pseudonymExtidInTag = tagValue;
		}

		if (pseudonymExtidInTag == null) {
			throw new IllegalStateException("Cannot get a pseudonym in a DICOM tag");
		}
		// TODO TELIMA-289
		// else {
		// if (destinationEntity.getSavePseudonym().booleanValue()) {
		// }
		// }
		return pseudonymExtidInTag;
	}

	public String getCacheExtid(PatientMetadata patientMetadata, Long projectID) {
		final String pseudonymCacheExtID = PatientClientUtil.getPseudonym(patientMetadata, externalIdCache, projectID);
		if (pseudonymCacheExtID == null) {
			throw new IllegalStateException("Cannot get an external pseudonym in cache");
		}
		return pseudonymCacheExtID;
	}

	// TODO TELIMA-289: methods to return pseudo for pseudo service or generate one (cf
	// git history + PseudonymApi.class)

}
