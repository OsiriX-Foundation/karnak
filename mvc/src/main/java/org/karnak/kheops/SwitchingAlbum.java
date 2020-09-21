package org.karnak.kheops;

import com.google.common.collect.ImmutableList;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.KheopsAlbums;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.WeakHashMap;

public class SwitchingAlbum {
    private final KheopsApi kheopsAPI;
    private WeakHashMap seriesUIDHashMap = new WeakHashMap<String, String>();

    public static final ImmutableList<String> MIN_SCOPE_SOURCE = ImmutableList.of("read", "send");
    public static final ImmutableList<String> MIN_SCOPE_DESTINATION = ImmutableList.of("write");

    public SwitchingAlbum() {
        kheopsAPI = new KheopsApi();
    }

    public void apply(KheopsAlbums kheopsAlbums, DicomObject dcm) {
        String authorizationSource = kheopsAlbums.getAuthorizationSource();
        String authorizationDestination = kheopsAlbums.getAuthorizationDestination();
        String condition = kheopsAlbums.getCondition();
        String studyInstanceUID = dcm.getStringOrElseThrow(Tag.StudyInstanceUID);
        String seriesInstanceUID = dcm.getStringOrElseThrow(Tag.SeriesInstanceUID);
        String API_URL = kheopsAlbums.getUrlAPI();
        if (condition == null || condition.length() == 0 || validateCondition(condition, dcm) &&
                seriesUIDHashMap.containsKey(seriesInstanceUID) == false) {
            final boolean validAuthorizationSource = validateToken(MIN_SCOPE_SOURCE, API_URL, authorizationSource);
            final boolean validDestinationSource = validateToken(MIN_SCOPE_DESTINATION, API_URL, authorizationDestination);

            if (validAuthorizationSource && validDestinationSource) {
                shareSerie(API_URL, studyInstanceUID, seriesInstanceUID, authorizationSource, authorizationDestination);
            }
        }
    }

    private boolean validateCondition(String condition, DicomObject dcm) {
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
