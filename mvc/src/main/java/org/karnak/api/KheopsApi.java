package org.karnak.api;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
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
import java.util.ArrayList;
import java.util.List;

// Link used:
// https://www.baeldung.com/httpclient-guide
// https://www.baeldung.com/httpclient4

public class KheopsApi {
    private final CloseableHttpClient client = HttpClientBuilder.create().build();
    private final String SERVER_URL;
    private final String X_AUTHORIZATION_SOURCE = "X-Authorization-Source";

    public KheopsApi(String SERVER_URL) {
        this.SERVER_URL = SERVER_URL;
    }
    // https://github.com/OsiriX-Foundation/KheopsAuthorization/wiki/Add-a-study
    // /studies/{StudyInstanceIUD}/albums/{album_id}
    // album_id: album id destination
    // x-authorization-source: main album token
    // Authorization: album destination token
    public int shareStudy(String albumID, String studyInstanceUID,
                          String authorizationSource, String authorizationDestination) throws IOException {
        final String stringURI = String.format("%s/studies/%s/albums/%s", SERVER_URL, studyInstanceUID, albumID);
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
        final String stringURI = String.format("%s/studies/%s/series/%s/albums/%s", SERVER_URL, studyInstanceUID, seriesInstanceUID, albumID);
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
        final String stringURI = String.format("%s/api/token/introspect", SERVER_URL);
        final URI uri = URI.create(stringURI);

        HttpPost request = new HttpPost(uri);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationToken);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", introspectToken));
        request.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

        CloseableHttpResponse response = client.execute(request);
        try {
            // Convert HttpEntity to JSONObject
            // https://stackoverflow.com/questions/10804466/how-to-convert-httpentity-into-json
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String retSrc = EntityUtils.toString(entity);
                return new JSONObject(retSrc);
            }
        } finally {
            response.close();
        }
        return null;
    }
}
