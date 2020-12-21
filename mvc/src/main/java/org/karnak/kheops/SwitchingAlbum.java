package org.karnak.kheops;

import com.google.common.collect.ImmutableList;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.expression.ExprConditionKheops;
import org.karnak.data.gateway.Project;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.expression.ExpressionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SwitchingAlbum {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchingAlbum.class);

    private final KheopsApi kheopsAPI;
    private Map<Long, List> switchingAlbumToDo = new WeakHashMap<>();

    public static final ImmutableList<String> MIN_SCOPE_SOURCE = ImmutableList.of("read", "send");
    public static final ImmutableList<String> MIN_SCOPE_DESTINATION = ImmutableList.of("write");

    public SwitchingAlbum() {
        kheopsAPI = new KheopsApi();
    }

    public void apply(Destination destination, KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String condition = kheopsAlbums.getCondition();
        HMAC hmac = generateHMAC(destination);
        String studyInstanceUID = hashUIDonDeidentification(destination, dcm.getStringOrElseThrow(Tag.StudyInstanceUID), hmac);
        String seriesInstanceUID = hashUIDonDeidentification(destination, dcm.getStringOrElseThrow(Tag.SeriesInstanceUID), hmac);
        String SOPInstanceUID = hashUIDonDeidentification(destination, dcm.getStringOrElseThrow(Tag.SOPInstanceUID), hmac);
        String API_URL = kheopsAlbums.getUrlAPI();
        Long id = kheopsAlbums.getId();
        if (!switchingAlbumToDo.containsKey(id)) {
            switchingAlbumToDo.put(id, new ArrayList());
        }
        ArrayList<MetadataSwitching> metadataToDo = (ArrayList<MetadataSwitching>) switchingAlbumToDo.get(id);

        if ((condition == null || condition.length() == 0 || validateCondition(condition, dcm)) &&
            metadataToDo.stream().noneMatch(metadataSwitching -> metadataSwitching.getSeriesInstanceUID().equals(seriesInstanceUID))) {
            final boolean validAuthorizationSource = validateToken(MIN_SCOPE_SOURCE, API_URL, authorizationSource);
            final boolean validDestinationSource = validateToken(MIN_SCOPE_DESTINATION, API_URL, authorizationDestination);

            if (validAuthorizationSource && validDestinationSource) {
                metadataToDo.add(new MetadataSwitching(studyInstanceUID, seriesInstanceUID, SOPInstanceUID));
            } else {
                LOGGER.warn("Can't validate a token for switching KHEOPS album [{}]. The series [{}] won't be shared.",
                        kheopsAlbums.getId(), seriesInstanceUID);
            }
        }
    }

    private static HMAC generateHMAC(Destination destination) {
        if (destination.getDesidentification()) {
            Project project = destination.getProject();
            return new HMAC(project.getSecret());
        }
        return null;
    }

    private static String hashUIDonDeidentification(Destination destination, String inputUID, HMAC hmac) {
        if (destination.getDesidentification() && hmac != null) {
            return hmac.uidHash(inputUID);
        }
        return inputUID;
    }

    private static boolean validateCondition(String condition, DicomObject dcm) {
        final ExprConditionKheops conditionKheops = new ExprConditionKheops(dcm);
        return (Boolean) ExpressionResult.get(condition, conditionKheops, Boolean.class);
    }

    private boolean validateToken(List<String> validMinScope, String API_URL, String introspectToken) {
        try {
            final JSONObject responseIntrospect = kheopsAPI.tokenIntrospect(API_URL, introspectToken, introspectToken);

            return validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            LOGGER.error("Invalid token", e);
            return false;
        }
    }

    public static boolean validateIntrospectedToken(JSONObject introspectObject, List<String> validMinScope) {
        boolean valid = true;
        if (!introspectObject.getBoolean("active")) {
            return false;
        }
        final String scope = introspectObject.getString("scope");
        for (String minScope : validMinScope) {
            valid = scope.contains(minScope) && valid;
        }
        return valid;
    }

    public void applyAfterTransfer(KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String SOPInstanceUID = dcm.getStringOrElseThrow(Tag.AffectedSOPInstanceUID);
        Long id = kheopsAlbums.getId();
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String API_URL = kheopsAlbums.getUrlAPI();

        ArrayList<MetadataSwitching> metadataToDo = (ArrayList<MetadataSwitching>) switchingAlbumToDo.get(id);
        metadataToDo.forEach(metadataSwitching -> {
            if (metadataSwitching.getSOPinstanceUID().equals(SOPInstanceUID) &&
                !metadataSwitching.isApplied()) {
                metadataSwitching.setApplied(true);
                int status = shareSerie(API_URL, metadataSwitching.getStudyInstanceUID(), metadataSwitching.getSeriesInstanceUID(),
                        authorizationSource, authorizationDestination);
                if (status > 299) {
                    LOGGER.warn("Can't share the serie [{}] for switching KHEOPS album [{}]. The response status is {}",
                            metadataSwitching.getSeriesInstanceUID(), id, status);
                }
            }
        });
    }

    private int shareSerie(String API_URL, String studyInstanceUID, String seriesInstanceUID,
                           String authorizationSource, String authorizationDestination) {
        try {
            return kheopsAPI.shareSerie(studyInstanceUID, seriesInstanceUID, API_URL,
                    authorizationSource, authorizationDestination);
        } catch (Exception e) {
            LOGGER.error("Can't share the serie {} in the study {}", seriesInstanceUID, studyInstanceUID, e);
        }
        return -1;
    }
}
