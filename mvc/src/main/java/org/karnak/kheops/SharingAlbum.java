package org.karnak.kheops;

import org.json.JSONObject;
import org.karnak.api.KheopsApi;

public class SharingAlbum {
    private KheopsApi kheopsApi;

    private String urlAPI = "https://test2.kheops.online/api";
    private String authorizationDestination = "XXXX";
    private String authorizationSource = "XXXX";
    private final String studyInstanceUID = "2.25.338816334367622307789824532505991004644";
    private final String seriesInstanceUID = "2.25.172154309332550182567617020430860508341";
    private final String albumID;

    public SharingAlbum() {
        kheopsApi = new KheopsApi(urlAPI);
        albumID = setAlbumID();
        series();
    }

    private String setAlbumID() {
        try {
            JSONObject tokenIntrospect = kheopsApi.tokenIntrospect(authorizationDestination, authorizationDestination);
            return tokenIntrospect.getString("album_id");
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public void series() {
        try {
            int status = kheopsApi.shareSerie(studyInstanceUID, seriesInstanceUID,
                    authorizationSource, authorizationDestination);
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
