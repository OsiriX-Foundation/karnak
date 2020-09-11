package org.karnak.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Link used:
// https://www.baeldung.com/httpclient-guide
// https://www.baeldung.com/httpclient4

public class KheopsApi {
    private final CloseableHttpClient client = HttpClientBuilder.create().build();
    private final String API_URL;
    private final String X_AUTHORIZATION_SOURCE = "X-Authorization-Source";

    public KheopsApi(String API_URL) {
        this.API_URL = API_URL;
    }
    // https://github.com/OsiriX-Foundation/KheopsAuthorization/wiki/Add-a-study
    // /studies/{StudyInstanceIUD}/albums/{album_id}
    // album_id: album id destination
    // x-authorization-source: main album token
    // Authorization: album destination token
    public int shareStudy(String albumID, String studyInstanceUID,
                          String authorizationSource, String authorizationDestination) throws IOException {
        final String stringURI = String.format("%s/studies/%s/albums/%s", API_URL, studyInstanceUID, albumID);
        final URI uri = URI.create(stringURI);

        HttpPut request = new HttpPut(uri);
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationDestination);
        request.setHeader(X_AUTHORIZATION_SOURCE, authorizationSource);

        CloseableHttpResponse response = client.execute(request);
        try {
            return response.getStatusLine().getStatusCode();
        } finally {
            response.close();
        }
    }

    // https://github.com/OsiriX-Foundation/KheopsAuthorization/wiki/Add-a-series
    // /studies/{StudyInstanceIUD}/series/{SeriesInstanceIUD}/albums/{album_id}
    // album_id: album id destination
    // x-authorization-source: main album token
    // Authorization: album destination token
    public int shareSerie(String albumID, String studyInstanceUID, String seriesInstanceUID,
                           String authorizationSource, String authorizationDestination) throws IOException {
        final String stringURI = String.format("%s/studies/%s/series/%s/albums/%s", API_URL, studyInstanceUID, seriesInstanceUID, albumID);
        final URI uri = URI.create(stringURI);

        HttpPut request = new HttpPut(uri);
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationDestination);
        request.setHeader(X_AUTHORIZATION_SOURCE, authorizationSource);

        CloseableHttpResponse response = client.execute(request);
        try {
            return response.getStatusLine().getStatusCode();
        } finally {
            response.close();
        }
    }

    // POST /api/token/introspect
    // Headers:
    // accept application/json
    // authorization token album destination
    // content-type: application/x-www-form-urlencoded
    // Form Data: token: 7GtWvR6XdbvEmtThvxy1my
    public JSONObject introspect(String authorizationToken, String introspectToken) throws IOException {
        // final String stringURI = String.format("%s/token/introspect", API_URL);
        final String stringURI = API_URL;
        final URI uri = URI.create(stringURI);

        HttpPost request = new HttpPost(uri);

        List<NameValuePair> form = new ArrayList<NameValuePair>();
        form.add(new BasicNameValuePair("token", introspectToken));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        request.setEntity(formEntity);

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationToken);

        CloseableHttpResponse response = client.execute(request);
        try {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status <= 300) {
                // Convert HttpEntity to JSONObject
                // https://stackoverflow.com/questions/10804466/how-to-convert-httpentity-into-json
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    System.out.println(EntityUtils.toString(responseEntity));
                    String retSrc = EntityUtils.toString(responseEntity);
                    return new JSONObject(retSrc);
                }
            } else {
                throw new ClientProtocolException(String.format("Unexpected response status: %d", status));
            }
        } finally {
            response.close();
        }
        return null;
    }
}
