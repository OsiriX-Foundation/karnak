/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.api;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.karnak.backend.api.rqbody.Body;
import org.karnak.backend.api.rqbody.Data;
import org.karnak.backend.api.rqbody.Fields;
import org.karnak.backend.api.rqbody.Ids;
import org.karnak.backend.api.rqbody.SearchIds;
import org.karnak.backend.config.MainzellisteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

/** API model */
public class PseudonymApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(PseudonymApi.class);

  private static final String SERVER_URL = MainzellisteConfig.getInstance().getServerurl();
  private static final String API_KEY = MainzellisteConfig.getInstance().getApikey();

  private static final String MAINZELLISTE_HEADER = "mainzellisteApiKey";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";

  private final HttpClient httpClient =
      HttpClient.newBuilder() // one instance, reuse
          .version(HttpClient.Version.HTTP_1_1)
          .build();

  private String sessionId;

  public String addExtID(Fields patientFields, String externalPseudonym) {
    this.sessionId = rqGetSessionId(true);
    String[] extid = {"pid", "extid"};
    Data data = new Data(extid, patientFields, new Ids(externalPseudonym));
    try {
      return getPseudonym(data);
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format("Cannot add a extid %s in Mainzellise %s", externalPseudonym, e));
    }
  }

  public String getExistingExtID(Fields patientFields) {
    this.sessionId = rqGetSessionId(true);
    String[] extid = {"pid", "extid"};
    Data data = new Data(extid, patientFields, null);
    try {
      return getPseudonym(data);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot get an existing extid in Mainzelliste API", e);
    }
  }

  public String generatePID(Fields patientFields) {
    this.sessionId = rqGetSessionId(true);
    String[] extid = {"pid"};
    Data data = new Data(extid, patientFields, null);
    try {
      return getPseudonym(data);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot generate pid in Mainzelliste", e);
    }
  }

  public JSONArray searchPatient(String pseudonym, String idTypes) {
    this.sessionId = rqGetSessionId(false);
    SearchIds[] searchIds = {new SearchIds(idTypes, pseudonym)}; // search example
    return getPatients(searchIds, false);
  }

  private String getPseudonym(Data data) throws IOException {
    String tokenIDAddPatient;
    try {
      tokenIDAddPatient = rqCreateTokenAddPatient(data);
      List<JSONObject> pseudonymList = rqCreatePatient(tokenIDAddPatient);
      String idTypes = data.get_idtypes()[data.get_idtypes().length - 1];
      JSONObject jsonPseudonym =
          pseudonymList.stream()
              .filter(p -> p.getString("idType").equals(idTypes))
              .findFirst()
              .orElseThrow();
      return jsonPseudonym.getString("idString");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException(e);
    }
  }

  /***
   * This classe allow the communcation betwen karnak and pseudonym api with a specific sessionId
   * @param sessionsId
   * public PseudonymApi(String sessionsId) {
   * this.sessionId = sessionsId;
   * }
   */

  /***
   * This method allow to construct a body in BodyPublisher format
   * @param data This param is the body in format HashMap with Key Value
   * @return BodyPublisher with the content of the data to pass in the body
   */
  private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
    var builder = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      if (entry != null) {
        if (builder.length() > 0) {
          builder.append("&");
        }
        builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
        builder.append("=");
        builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
      }
    }

    return HttpRequest.BodyPublishers.ofString(builder.toString());
  }

  /***
   * Get patient in pseudonym api
   * @param searchIds
   * @param shouldThrowException Check if an exception should be thrown or only a log
   * @return Pseudonym
   */
  private JSONArray getPatients(SearchIds[] searchIds, boolean shouldThrowException) {
    JSONArray patientArray = null;
    try {
      String tokenId = rqCreateTokenReadPatient(searchIds, shouldThrowException);
      patientArray = rqGetPatient(tokenId, shouldThrowException);
    } catch (InterruptedException e) {
      LOGGER.warn("Session interrupted. Cannot create patient", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOGGER.info("Cannot get patient", e);
    }
    return patientArray;
  }

  /***
   * Make the request to have an id session to the API that manages the pseudonyms
   * @param shouldThrowException Check if an exception should be thrown or only a log
   * @return sessionID
   */
  private String rqGetSessionId(boolean shouldThrowException) {
    Map<Object, Object> data = new HashMap<>();
    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(buildFormDataFromMap(data))
            .uri(URI.create(SERVER_URL + "/sessions"))
            .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON_VALUE)
            .header(MAINZELLISTE_HEADER, API_KEY)
            .build();

    HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString());
      if (controlErrorResponse(response, shouldThrowException)) {
        JSONObject jsonResp = new JSONObject(response.body());
        this.sessionId = jsonResp.getString("sessionId");
      }
    } catch (InterruptedException e) {
      LOGGER.warn("Session interrupted with Mainzelliste API", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOGGER.error("Cannot get a sessionId with Mainzelliste API", e);
    }
    return this.sessionId;
  }

  /***
   * Make the request to have a token that allow to add a new patient
   * @return sessionID
   */
  private String rqCreateTokenAddPatient(Data data) throws IOException, InterruptedException {
    Body bodyRequest = new Body("addPatient", data);
    Gson gson = new Gson();
    String jsonBody = gson.toJson(bodyRequest);

    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString(jsonBody))
            .uri(URI.create(SERVER_URL + "/sessions/" + this.sessionId + "/tokens"))
            .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON_VALUE)
            .header(MAINZELLISTE_HEADER, API_KEY)
            .build();

    final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    controlErrorResponse(response, true);
    final JSONObject jsonResp = new JSONObject(response.body());
    return jsonResp.getString("tokenId");
  }

  /***
   * Make the request to have a token that allow to get patient(s)
   * @param searchIds
   * @param shouldThrowException Check if an exception should be thrown or only a log
   * @return Patients
   */
  private String rqCreateTokenReadPatient(SearchIds[] searchIds, boolean shouldThrowException)
      throws IOException, InterruptedException {
    String tokenId = null;
    String jsonBody = createJsonReadPatient(searchIds);
    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString(jsonBody))
            .uri(URI.create(SERVER_URL + "/sessions/" + this.sessionId + "/tokens"))
            .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON_VALUE)
            .header(MAINZELLISTE_HEADER, API_KEY)
            .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    if (controlErrorResponse(response, shouldThrowException)) {
      JSONObject jsonResp = new JSONObject(response.body());
      tokenId = jsonResp.getString("tokenId");
    }

    return tokenId;
  }

  /***
   * Make the request to create patient with the tokenId
   * @param tokenId
   * @return Pseudonym
   */
  private List<JSONObject> rqCreatePatient(String tokenId)
      throws IOException, InterruptedException {
    Map<Object, Object> data = new HashMap<>();
    data.put("sureness", true);
    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(buildFormDataFromMap(data))
            .uri(
                URI.create(
                    SERVER_URL + "/patients?tokenId=" + tokenId + "&mainzellisteApiVersion=2.0"))
            .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header(MAINZELLISTE_HEADER, API_KEY)
            .build();

    List<JSONObject> newIds = new ArrayList<>();
    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    controlErrorResponse(response, true);
    JSONArray jsonResp = new JSONArray(response.body());
    for (Object o : jsonResp) {
      if (o instanceof JSONObject) {
        newIds.add((JSONObject) o);
      }
    }

    return newIds;
  }

  /***
   * Make the request to get patient with the tokenId
   * @param tokenId
   * @param shouldThrowException Check if an exception should be thrown or only a log
   * @return Pseudonym
   */
  private JSONArray rqGetPatient(String tokenId, boolean shouldThrowException)
      throws IOException, InterruptedException {
    JSONArray jsonArray = null;

    HttpRequest request =
        HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(SERVER_URL + "/patients?tokenId=" + tokenId))
            .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header(MAINZELLISTE_HEADER, API_KEY)
            .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    if (controlErrorResponse(response, shouldThrowException)) {
      jsonArray = new JSONArray(response.body());
    }
    return jsonArray;
  }

  /***
   * This method allow to create a json body for readPatients in pseudonym api
   * @param searchIds SearchIds that we want to read in pseudonym api.
   * @return String json body
   */
  private String createJsonReadPatient(SearchIds[] searchIds) {
    String[] resultFields = {
      "patientID", "patientName", "patientBirthDate", "patientSex", "issuerOfPatientID"
    }; // fields returns
    Data data = new Data(searchIds, resultFields);

    Body bodyRequest = new Body("readPatients", data);
    Gson gson = new Gson();
    return gson.toJson(bodyRequest);
  }

  /**
   * Control response
   *
   * @param response Response
   * @param throwError should an exception should be thrown
   * @return true if no problem in response, false otherwise
   */
  private boolean controlErrorResponse(HttpResponse<String> response, boolean throwError) {
    boolean status = true;
    if (response.statusCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
      final String errorMsg =
          "\n\tMainzelliste response : "
              + "\n\t\tstatus code: "
              + response.statusCode()
              + "\n\t\theaders:: "
              + response.headers()
              + "\n\t\tbody: "
              + response.body();
      if (throwError) {
        throw new IllegalStateException(errorMsg);
      } else {
        LOGGER.info(errorMsg);
        status = false;
      }
    }
    return status;
  }
}
