package org.karnak.kheops;

import org.json.JSONObject;
import org.karnak.api.KheopsApi;

public class SharingAlbum {
    private KheopsApi kheopsApi;

    private String urlAPI = "http://httpbin.org/post";
    private String authorizationDestination = "XXXX";
    private String authorizationSource = "YYYYY";
    private final String albumID;

    public SharingAlbum() {
        kheopsApi = new KheopsApi(urlAPI);
        albumID = setAlbumID();
    }

    private String setAlbumID() {
        try {
            JSONObject introspect = kheopsApi.introspect(authorizationDestination, authorizationDestination);
            return introspect.getString("album_id");
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public void series() {

    }
}
