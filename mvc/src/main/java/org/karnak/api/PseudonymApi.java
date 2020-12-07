package org.karnak.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.karnak.api.rqbody.Body;
import org.karnak.api.rqbody.Data;
import org.karnak.api.rqbody.Ids;
import org.karnak.api.rqbody.SearchIds;
import org.karnak.api.rqbody.Fields;
import org.karnak.data.MainzellisteConfig;
import org.karnak.data.gateway.IdTypes;
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

    private final String SERVER_URL = MainzellisteConfig.getInstance().getServerurl();
    private final String API_KEY = MainzellisteConfig.getInstance().getApikey();
    private final String ID_TYPES = MainzellisteConfig.getInstance().getIdtypes();
    private final String EXTERNAL_ID = "extid";
    private final String externalPseudonym;

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
        this.externalPseudonym = null;
    }

    public PseudonymApi(String externalPseudonym) {
        this.sessionId = rqGetSessionId();
        this.externalPseudonym = externalPseudonym;
    }

    /***
     * This classe allow the communcation betwen karnak and pseudonym api with a specific sessionId
     * @param sessionsId
    public PseudonymApi(String sessionsId) {
        this.sessionId = sessionsId;
    }
     */

    /***
     * Get patient info with pseudonym
     * @param pseudonym 
     * @return patient
     */
    public JSONArray searchPatient(String pseudonym){
        SearchIds [] searchIds = {new SearchIds(ID_TYPES, pseudonym)}; //search example
        JSONArray patientsReturns = getPatients(searchIds);
        return patientsReturns;
    }

    /***
     * Create patient in pseudonym api
     * @param patientFields 
     * @return Pseudonym
     */
    public String createPatient(Fields patientFields, IdTypes idTypes) throws IOException, InterruptedException {
        String tokenId = rqCreateTokenAddPatient(patientFields, idTypes);
        List<JSONObject> pseudonymList = rqCreatePatient(tokenId);
        JSONObject jsonPseudonym = pseudonymList.stream().filter(p -> p.getString("idType").equals(idTypes.getValue())).findFirst().orElseThrow();
        return jsonPseudonym.getString("idString");
    }

    /***
     * Get patient in pseudonym api
     * @param searchIds 
     * @return Pseudonym
     */
    public JSONArray getPatients(SearchIds [] searchIds){
        JSONArray patientArray=null;    
        try{
            String tokenId = rqCreateTokenReadPatient(searchIds);
            patientArray = rqGetPatient(tokenId);
        }catch (Exception e){
            log.error("Cannot create patient", e);
        } 
        return patientArray;
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
            controlErrorResponse(response);
            final JSONObject jsonResp = new JSONObject(response.body());
            this.sessionId = jsonResp.getString("sessionId");
        } catch (Exception e) {
            log.error("Cannot get a sessionId with Mainzelliste API {}", e);
        }
        return this.sessionId;
    }

    /***
     * Make the request to have a token that allow to add a new patient
     * @return sessionID
     */
    public String rqCreateTokenAddPatient(Fields patientFields, IdTypes idTypes) throws IOException, InterruptedException {
        String jsonBody = createJsonRequest(patientFields, idTypes);
        HttpRequest request = HttpRequest.newBuilder()
        .POST(BodyPublishers.ofString(jsonBody))
        .uri(URI.create(this.SERVER_URL + "/sessions/"+this.sessionId+"/tokens"))
        .header("Content-Type", "application/json")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        controlErrorResponse(response);
        final JSONObject jsonResp = new JSONObject(response.body());
        String tokenAddPatient = jsonResp.getString("tokenId");

        return tokenAddPatient;
    }

    /***
     * Make the request to have a token that allow to get patient(s)
     * @param searchIds
     * @return Patients
     */
    public String rqCreateTokenReadPatient(SearchIds [] searchIds) throws IOException, InterruptedException {
        String jsonBody = createJsonReadPatient(searchIds);        
        HttpRequest request = HttpRequest.newBuilder()
        .POST(BodyPublishers.ofString(jsonBody))
        .uri(URI.create(this.SERVER_URL + "/sessions/"+this.sessionId+"/tokens"))
        .header("Content-Type", "application/json")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        controlErrorResponse(response);

        final JSONObject jsonResp = new JSONObject(response.body());
        String tokenReadPatient = jsonResp.getString("tokenId");

        return tokenReadPatient;
    }

    /***
     * Make the request to create patient with the tokenId
     * @param tokenId 
     * @return Pseudonym
     */
    public List<JSONObject> rqCreatePatient(String tokenId) throws IOException, InterruptedException {
        Map<Object, Object> data = new HashMap<>();
        data.put("sureness", true);
        HttpRequest request = HttpRequest.newBuilder()
        .POST(buildFormDataFromMap(data))
        .uri(URI.create(this.SERVER_URL + "/patients?tokenId="+tokenId + "&mainzellisteApiVersion=2.0"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        List<JSONObject> newIds = new ArrayList<>();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        controlErrorResponse(response);
        final JSONArray jsonResp = new JSONArray(response.body());
        for (Object o: jsonResp) {
            if ( o instanceof JSONObject ) {
                newIds.add((JSONObject)o);
            }
        }

        return newIds;
    }

    /***
     * Make the request to get patient with the tokenId
     * @param tokenId 
     * @return Pseudonym
     */
    public JSONArray rqGetPatient(String tokenId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(this.SERVER_URL + "/patients?tokenId="+tokenId))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("mainzellisteApiKey", this.API_KEY)
        .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        controlErrorResponse(response);
        JSONArray jsonResp = new JSONArray(response.body());

        return jsonResp;
    }

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
     * This method allow to create a json body for addPatient in pseudonym api
     * @param patientFields Patient that we want to add in pseudonym api.
     * @return String json body
     */
    private String createJsonRequest(Fields patientFields, IdTypes idTypes) {
        final Fields field = patientFields;
        final String [] pid = {ID_TYPES};    //pseudonymisation type
        final String [] extid = {ID_TYPES, EXTERNAL_ID};

        Data data; // = new Data(externalPseudonym == null ? idTypes : idTypesExternal, field, externalPseudonym == null ? null : new Ids(externalPseudonym));
        switch (idTypes) {
            case ADD_EXTID: data = new Data(extid, field, new Ids(externalPseudonym));
            break;
            case EXTID: data = new Data(extid, field, null);
            break;
            case PID: data = new Data(pid, field, null);
            break;
            default: data = new Data(pid, field, null);
        }

        final Body bodyRequest= new Body("addPatient", data);
        final Gson gson = new Gson();
        return gson.toJson(bodyRequest);
    }

    /***
     * This method allow to create a json body for readPatients in pseudonym api
     * @param searchIds SearchIds that we want to read in pseudonym api.
     * @return String json body
     */
    private String createJsonReadPatient(SearchIds [] searchIds) {
        String [] resultFields = {"patientID","patientName", "patientBirthDate", "patientSex", "issuerOfPatientID"};    //fields returns
        Data data = new Data(searchIds, resultFields); 

        Body bodyRequest= new Body("readPatients", data);
        Gson gson = new Gson();
        return gson.toJson(bodyRequest);
    }

    private void controlErrorResponse(HttpResponse<String> response) {
        if(response.statusCode() > 299) {
            final String errorMsg = "\n\tMainzelliste response : " +
                    "\n\t\tstatus code: " + response.statusCode() +
                    "\n\t\theaders:: " + response.headers() +
                    "\n\t\tbody: " + response.body();
            throw new IllegalStateException(errorMsg);
        }
    }

}