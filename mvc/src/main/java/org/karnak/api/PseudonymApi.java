package org.karnak.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.karnak.api.rqbody.Body;
import org.karnak.api.rqbody.Data;
import org.karnak.api.rqbody.Ids;
import org.karnak.api.rqbody.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API model
 */
public class PseudonymApi {
    // ---------------------------------------------------------------
    // Constants -----------------------------------------------------
    // ---------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(PseudonymApi.class);
    private final String SERVER_URL = "http://localhost:8080";
    private final String API_KEY = "changeThisApiKey";

    private final HttpClient httpClient = HttpClient.newBuilder() // one instance, reuse
            .version(HttpClient.Version.HTTP_2).build();

    // ---------------------------------------------------------------
    // Fields --------------------------------------------------------
    // ---------------------------------------------------------------
    private String sessionId;

    /***
     * This classe allow the communcation betwen karnak and pseudonym api. Get sessionId at initialization.
     */
    public PseudonymApi() {
        this.sessionId = rqGetSessionId();
    }

    /***
     * This classe allow the communcation betwen karnak and pseudonym api with a specific sessionId
     * @param sessionsId 
     */
    public PseudonymApi(String sessionsId) {
        this.sessionId = sessionsId;
    }


    /***
     * Create patient in pseudonym api
     * @param tokenId 
     * @return Pseudonym
     */
    public String createPatient(Fields patientFields){
        String pseudonym="";    
        try{
            String tokenId = rqCreateTokenAddPatient(patientFields);
            pseudonym = rqCreatePatient(tokenId);
        }catch (Exception e){
            log.error("Cannot create patient", e);
        } 
        return pseudonym;
    }



    /***
     * Make the request to have an id session to the API that manages the pseudonyms
     * @return sessionID
     */
    public String rqGetSessionId() {
        Map<Object, Object> data = new HashMap<>();
        HttpRequest request = HttpRequest.newBuilder()
        .POST(buildFormDataFromMap(data))
        .uri(URI.create(this.SERVER_URL + "/sessions"))
        .header("Content-Type", "application/json")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());

            final JSONObject jsonResp = new JSONObject(response.body());
            this.sessionId = jsonResp.getString("sessionId");
        } catch (Exception e) {
            log.error("Cannot get a sessionId request {}", e);
        }
        return this.sessionId;
    }

    /***
     * Make the request to have a token that allow to add a new patient
     * @return sessionID
     */
    public String rqCreateTokenAddPatient(Fields patientFields) {
        String jsonBody = createJsonRequest(patientFields);        
        HttpRequest request = HttpRequest.newBuilder()
        .POST(BodyPublishers.ofString(jsonBody))
        .uri(URI.create(this.SERVER_URL + "/sessions/"+this.sessionId+"/tokens"))
        .header("Content-Type", "application/json")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        String tokenAddPatient="";
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());

            final JSONObject jsonResp = new JSONObject(response.body());
            tokenAddPatient = jsonResp.getString("tokenId");
        } catch (Exception e) {
            log.error("Cannot create a token request for addPatient {}", e);
        }
        return tokenAddPatient;
    }

    /***
     * Make the request to create patient with the tokenId
     * @param tokenId 
     * @return Pseudonym
     */
    public String rqCreatePatient(String tokenId) {
        Map<Object, Object> data = new HashMap<>();
        HttpRequest request = HttpRequest.newBuilder()
        .POST(buildFormDataFromMap(data))
        .uri(URI.create(this.SERVER_URL + "/patients?tokenId="+tokenId))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        String newId="";
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());

            final JSONObject jsonResp = new JSONObject(response.body());
            newId = jsonResp.getString("newId");
        } catch (Exception e) {
            log.error("Cannot create patient request {}", e);
        }
        return newId;
    }

    /***
     * This method allow to construct a body in BodyPublisher format
     * @param data This param is the body in format HashMap with Key Value
     * @return BodyPublisher with the content of the data to pass in the body
     */
    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    /***
     * This method allow to create a json body for addPatient in pseudonym api
     * @param patient Patient that we want to add in pseudonym api.
     * @return String json body
     */
    private String createJsonRequest(Fields patientFields) {
        Fields field = patientFields;
        Ids ids = new Ids (""); // IDS not use 
        String [] idtypes = {"pid"};    //pseudonymisation type
        Data data = new Data(idtypes, field, ids, "http://callbacklistener:8887"); 
        Body bodyRequest= new Body("addPatient", data);
        Gson gson = new Gson();
        return gson.toJson(bodyRequest);
    }

}