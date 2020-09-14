package org.karnak.kheops;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.WeakHashMap;

public class SwitchingAlbum {
    private final KheopsApi kheopsAPI;
    private WeakHashMap seriesUIDHashMap = new WeakHashMap<String, String>();

    public SwitchingAlbum() {
        kheopsAPI = new KheopsApi();
    }

    public void shareSerie(KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String studyInstanceUID = dcm.getStringOrElseThrow(Tag.StudyInstanceUID);
        String seriesInstanceUID = dcm.getStringOrElseThrow(Tag.SeriesInstanceUID);
        String API_URL = kheopsAlbums.getUrlAPI();
        try {
            //TODO: If condition is true { ... }
            if (seriesUIDHashMap.containsKey(seriesInstanceUID) == false) {
                int status = kheopsAPI.shareSerie(studyInstanceUID, seriesInstanceUID, API_URL,
                        authorizationSource, authorizationDestination);
                seriesUIDHashMap.put(seriesInstanceUID, "send");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
