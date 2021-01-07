package org.karnak.backend.service.kheops;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.json.JSONObject;
import org.karnak.backend.api.KheopsApi;
import org.karnak.backend.data.entity.Destination;
import org.karnak.backend.data.entity.KheopsAlbums;
import org.karnak.backend.data.entity.Project;
import org.karnak.backend.model.expression.ExprConditionKheops;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.model.kheops.MetadataSwitching;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchingAlbum {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchingAlbum.class);

    private final KheopsApi kheopsAPI;
    private final Map<Long, List> switchingAlbumToDo = new WeakHashMap<>();

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
        String sopInstanceUID = hashUIDonDeidentification(destination, dcm.getStringOrElseThrow(Tag.SOPInstanceUID), hmac);
        String urlAPI = kheopsAlbums.getUrlAPI();
        Long id = kheopsAlbums.getId();
        if (!switchingAlbumToDo.containsKey(id)) {
            switchingAlbumToDo.put(id, new ArrayList());
        }
        ArrayList<MetadataSwitching> metadataToDo = (ArrayList<MetadataSwitching>) switchingAlbumToDo.get(id);

        if ((condition == null || condition.length() == 0 || validateCondition(condition, dcm)) &&
            metadataToDo.stream().noneMatch(metadataSwitching -> metadataSwitching.getSeriesInstanceUID().equals(seriesInstanceUID))) {
            final boolean validAuthorizationSource = validateToken(MIN_SCOPE_SOURCE, urlAPI, authorizationSource);
            final boolean validDestinationSource = validateToken(MIN_SCOPE_DESTINATION, urlAPI, authorizationDestination);

            if (validAuthorizationSource && validDestinationSource) {
                metadataToDo.add(new MetadataSwitching(studyInstanceUID, seriesInstanceUID, sopInstanceUID));
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

    private boolean validateToken(List<String> validMinScope, String urlAPI, String introspectToken) {
        try {
            final JSONObject responseIntrospect = kheopsAPI.tokenIntrospect(urlAPI, introspectToken, introspectToken);

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
        String sopInstanceUID = dcm.getStringOrElseThrow(Tag.AffectedSOPInstanceUID);
        Long id = kheopsAlbums.getId();
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String urlAPI = kheopsAlbums.getUrlAPI();

        ArrayList<MetadataSwitching> metadataToDo = (ArrayList<MetadataSwitching>) switchingAlbumToDo.get(id);
        metadataToDo.forEach(metadataSwitching -> {
            if (metadataSwitching.getSOPinstanceUID().equals(sopInstanceUID) &&
                !metadataSwitching.isApplied()) {
                metadataSwitching.setApplied(true);
                int status = shareSerie(urlAPI, metadataSwitching.getStudyInstanceUID(), metadataSwitching.getSeriesInstanceUID(),
                        authorizationSource, authorizationDestination);
                if (status > 299) {
                    LOGGER.warn("Can't share the serie [{}] for switching KHEOPS album [{}]. The response status is {}",
                            metadataSwitching.getSeriesInstanceUID(), id, status);
                }
            }
        });
    }

    private int shareSerie(String urlAPI, String studyInstanceUID, String seriesInstanceUID,
                           String authorizationSource, String authorizationDestination) {
        try {
            return kheopsAPI.shareSerie(studyInstanceUID, seriesInstanceUID, urlAPI,
                    authorizationSource, authorizationDestination);
        } catch (Exception e) {
            LOGGER.error("Can't share the serie {} in the study {}", seriesInstanceUID, studyInstanceUID, e);
        }
        return -1;
    }
}
