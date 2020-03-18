package org.karnak.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * API model
 */
public class PseudonymApi {
    // ---------------------------------------------------------------
    // Constants ----------------------------------------------------
    // ---------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(PseudonymApi.class);
    private final String SERVER_URL = "http://localhost:8080";
    private final String API_KEY = "changeThisApiKey";

    private final HttpClient httpClient = HttpClient.newBuilder() // one instance, reuse
            .version(HttpClient.Version.HTTP_2).build();

    // ---------------------------------------------------------------
    // Fields -------------------------------------------------------
    // ---------------------------------------------------------------
    private String sessionsId;
    private String createPatientToken;
    private String getPatientToken;

    /***
     * @desc This classe allow the communcation betwen karnak and pseudonym api
     */
    public PseudonymApi() {
        this.sessionsId = getSession();
    }

    /***
     * @desc Api 
     * @param sessionsId This classe allow the communcation betwen karnak and pseudonym api
     */
    public PseudonymApi(String sessionsId) {
        this.sessionsId = sessionsId;
    }

    /***
     * @desc Get session ID
     */
    public String getSession() {
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

            final JSONObject obj = new JSONObject(response.body());
            this.sessionsId = obj.getString("sessionId");
        } catch (Exception e) {
            log.error("Cannot gest a sessionId in pseudonym api {}", e);
        }
        return this.sessionsId;
    }

    /***
     * @desc This method allow to construct a body in BodyPublisher format
     * @param data This param is the body in format HashMap with Key Value
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

}