package org.karnak.kheops;

import com.google.common.collect.ImmutableList;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.List;
import java.util.WeakHashMap;

public class SwitchingAlbum {
    private final KheopsApi kheopsAPI;
    private WeakHashMap seriesUIDHashMap = new WeakHashMap<String, String>();

    private static final ImmutableList<String> MIN_SCOPE_SOURCE = ImmutableList.of("read", "send");
    private static final ImmutableList<String> MIN_SCOPE_DESTINATION = ImmutableList.of("write");

    public SwitchingAlbum() {
        kheopsAPI = new KheopsApi();
    }

    public void apply(KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String studyInstanceUID = dcm.getStringOrElseThrow(Tag.StudyInstanceUID);
        String seriesInstanceUID = dcm.getStringOrElseThrow(Tag.SeriesInstanceUID);
        String API_URL = kheopsAlbums.getUrlAPI();

        if (seriesUIDHashMap.containsKey(seriesInstanceUID) == false) {
            final boolean validAuthorizationSource = validateToken(MIN_SCOPE_SOURCE, API_URL, authorizationSource);
            final boolean validDestinationSource = validateToken(MIN_SCOPE_DESTINATION, API_URL, authorizationDestination);

            if (validAuthorizationSource && validDestinationSource) {
            //TODO: If condition is true { ... }
                shareSerie(API_URL, studyInstanceUID, seriesInstanceUID, authorizationSource, authorizationDestination);
            }
        }
    }

    private boolean validateToken(List<String> validMinScope, String API_URL, String introspectToken) {
        boolean valid = true;
        try {
            final JSONObject responseIntrospect = kheopsAPI.tokenIntrospect(API_URL, introspectToken, introspectToken);

            if (responseIntrospect.getBoolean("active") == false) {
                return false;
            }

            final String scope = responseIntrospect.getString("scope");
            for (String minScope : validMinScope) {
                valid = scope.contains(minScope) && valid;
            }
            return valid;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    private void shareSerie(String API_URL, String studyInstanceUID, String seriesInstanceUID,
                           String authorizationSource, String authorizationDestination) {
        try {
            int status = kheopsAPI.shareSerie(studyInstanceUID, seriesInstanceUID, API_URL,
                    authorizationSource, authorizationDestination);
            seriesUIDHashMap.put(seriesInstanceUID, "send");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
