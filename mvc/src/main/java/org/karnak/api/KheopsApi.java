package org.karnak.api;

import org.apache.http.HttpHeaders;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

// Link used:
// https://www.baeldung.com/httpclient-guide
// https://www.baeldung.com/httpclient4

public class KheopsApi {
    private final HttpClient client = HttpClient.newBuilder() // one instance, reuse
            .version(HttpClient.Version.HTTP_2).build();
    private final String API_URL;
    private final String X_AUTHORIZATION_SOURCE = "X-Authorization-Source";

    public KheopsApi(String API_URL) {
        this.API_URL = API_URL;
    }

    // https://github.com/OsiriX-Foundation/KheopsAuthorization/wiki/Add-a-series
    // /studies/{StudyInstanceIUD}/series/{SeriesInstanceIUD}/albums/{album_id}
    // album_id: album id destination
    // x-authorization-source: main album token
    // Authorization: album destination token
    public int shareSerie(String studyInstanceUID, String seriesInstanceUID,
                          String authorizationSource, String authorizationDestination) throws IOException, InterruptedException {
        final String stringURI = String.format("%s/studies/%s/series/%s", API_URL, studyInstanceUID, seriesInstanceUID);
        final URI uri = URI.create(stringURI);
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(uri)
                .setHeader(HttpHeaders.ACCEPT, "application/json")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", authorizationDestination))
                .setHeader(X_AUTHORIZATION_SOURCE, String.format("Bearer %s", authorizationSource))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            return response.statusCode();
        } catch (Exception e) {
            System.err.println(e);
        }
        return -1;
    }

    public JSONObject tokenIntrospect(String authorizationToken, String introspectToken) throws IOException, InterruptedException {
        final String stringURI = String.format("%s/token/introspect", API_URL);
        final URI uri = URI.create(stringURI);

        Map<Object, Object> data = new HashMap<>();
        data.put("token", introspectToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(utils.buildDataFromMap(data))
                .uri(uri)
                .setHeader(HttpHeaders.ACCEPT, "application/json")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", authorizationToken))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            final int status = response.statusCode();
            if (status >= 200 && status <= 300) {
                return new JSONObject(response.body());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

}
