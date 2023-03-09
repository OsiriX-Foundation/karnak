/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.cache.Patient;
import org.karnak.backend.dicom.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PseudonymMappingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PseudonymMappingService.class);

	public Patient retrieveMainzellistePatient(final String pseudonym) {
		Patient mainzellistePatient = null;

		// Pseudonym api
		PseudonymApi pseudonymApi = new PseudonymApi();

		// Search pid pseudonym
		JSONArray patientFoundJSONArray = pseudonymApi.searchPatient(pseudonym, "pid");

		// Not found: Search extid pseudonym
		if (patientFoundJSONArray == null) {
			patientFoundJSONArray = pseudonymApi.searchPatient(pseudonym, "extid");
		}

		// Patient found
		if (patientFoundJSONArray != null && !patientFoundJSONArray.isEmpty() && !patientFoundJSONArray.isNull(0)) {
			// Retrieve patient from response
			JSONObject jsonObject = ((JSONObject) patientFoundJSONArray.getJSONObject(0).get("fields"));

			// Map to model
			if (jsonObject != null) {
				mainzellistePatient = new Patient(pseudonym, jsonObject.getString("patientID"), null, null,
						DateTimeUtils.parseDA(jsonObject.getString("patientBirthDate")),
						jsonObject.getString("patientSex"), jsonObject.getString("issuerOfPatientID"));
				// Set patient name (first/last)
				mainzellistePatient.updatePatientName(jsonObject.getString("patientName"));
			}
		}
		return mainzellistePatient;
	}

}
