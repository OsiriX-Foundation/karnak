package org.karnak.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.karnak.backend.api.PseudonymApi;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.dicom.DateTimeUtils;
import org.springframework.stereotype.Service;

@Service
public class PseudonymMappingService {

  public MainzellistePatient retrieveMainzellistePatient(final String pseudonym) {
    MainzellistePatient mainzellistePatient = null;

    // Pseudonym api
    PseudonymApi pseudonymApi = new PseudonymApi();

    // Search pid pseudonym
    JSONArray patientFoundJSONArray = pseudonymApi.searchPatient(pseudonym, "pid");

    // Not found: Search extid pseudonym
    if (patientFoundJSONArray == null) {
      patientFoundJSONArray = pseudonymApi.searchPatient(pseudonym, "extid");
    }

    // Patient found
    if (patientFoundJSONArray != null
        && !patientFoundJSONArray.isEmpty()
        && !patientFoundJSONArray.isNull(0)) {
      // Retrieve patient from response
      JSONObject jsonObject = ((JSONObject) patientFoundJSONArray.getJSONObject(0).get("fields"));

      // Map to model
      if (jsonObject != null) {
        mainzellistePatient =
            new MainzellistePatient(
                pseudonym,
                jsonObject.getString("patientID"),
                null,
                null,
                DateTimeUtils.parseDA(jsonObject.getString("patientBirthDate")),
                jsonObject.getString("patientSex"),
                jsonObject.getString("issuerOfPatientID"));
        // Set patient name (first/last)
        mainzellistePatient.updatePatientName(jsonObject.getString("patientName"));
      }
    }
    return mainzellistePatient;
  }
}
