package org.karnak.kheops;

import com.google.common.collect.ImmutableList;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.AppConfig;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.profilepipe.utils.HMAC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

public class SwitchingAlbum {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchingAlbum.class);

    private final KheopsApi kheopsAPI;
    private Map<Long, List> switchingAlbumToDo = new WeakHashMap<>();

    private final String KEY_SERIES_INSTANCE_UID = "seriesInstanceUID";
    private final String KEY_SOP_INSTANCE_UID = "SOPInstanceUID";

    public static final ImmutableList<String> MIN_SCOPE_SOURCE = ImmutableList.of("read", "send");
    public static final ImmutableList<String> MIN_SCOPE_DESTINATION = ImmutableList.of("write");

    public SwitchingAlbum() {
        kheopsAPI = new KheopsApi();
    }

    public void apply(Destination destination, KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String condition = kheopsAlbums.getCondition();
        String patientID = dcm.getStringOrElseThrow(Tag.PatientID);
        String studyInstanceUID = hashUIDonDeidentification(destination, patientID, dcm.getStringOrElseThrow(Tag.StudyInstanceUID));
        String seriesInstanceUID = hashUIDonDeidentification(destination, patientID, dcm.getStringOrElseThrow(Tag.SeriesInstanceUID));
        String SOPInstanceUID = dcm.getStringOrElseThrow(Tag.SOPInstanceUID);
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

    private static String hashUIDonDeidentification(Destination destination, String inputPatientID, String inputUID) {
        if (destination.getDesidentification()) {
            final String PatientIDProfile = HMAC.generatePatientIDProfile(inputPatientID, destination);
            return AppConfig.getInstance().getHmac().uidHash(PatientIDProfile, inputUID);
        }
        return inputUID;
    }

    private static boolean validateCondition(String condition, DicomObject dcm) {
        final ExprConditionKheops conditionKheops = new ExprConditionKheops(dcm);
        return getResultCondition(condition, conditionKheops);
    }

    private static boolean getResultCondition(String condition, ExprConditionKheops exprConditionKheops){
        try {
            //https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
            final ExpressionParser parser = new SpelExpressionParser();
            final EvaluationContext context = new StandardEvaluationContext(exprConditionKheops);
            final String cleanCondition = exprConditionKheops.conditionInterpreter(condition);
            context.setVariable("VR", VR.class);
            context.setVariable("Tag", Tag.class);
            final Expression exp = parser.parseExpression(cleanCondition);
            boolean valid = exp.getValue(context, Boolean.class);
            return valid;
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean validateToken(List<String> validMinScope, String API_URL, String introspectToken) {
        try {
            final JSONObject responseIntrospect = kheopsAPI.tokenIntrospect(API_URL, introspectToken, introspectToken);

            return validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            System.err.println(e);
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
                metadataSwitching.isApplied() == false) {
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
            System.err.println(e);
        }
        return -1;
    }
}
