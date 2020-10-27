package org.karnak.expression;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
import org.weasis.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExprConditionKheops implements ExpressionItem{
    private DicomObject dcm;

    public ExprConditionKheops(DicomObject dcm) {
        this.dcm = dcm;
    }

    public boolean tagValueIsPresent(int tag, String value) {
        String dcmValue = dcm.getString(tag).orElse(null);
        return dcmValue != null ? dcmValue.equals(value) : false;
    }

    public boolean tagValueContains(int tag, String value) {
        String dcmValue = dcm.getString(tag).orElse(null);
        return dcmValue != null ? dcmValue.contains(value) : false;
    }

    public boolean tagValueBeginWith(int tag, String value) {
        String dcmValue = dcm.getString(tag).orElse(null);
        return dcmValue != null ? dcmValue.startsWith(value) : false;
    }

    public boolean tagValueEndWith(int tag, String value) {
        String dcmValue = dcm.getString(tag).orElse(null);
        return dcmValue != null ? dcmValue.endsWith(value) : false;
    }

    public static void expressionValidation(String condition) throws Exception {
        ExprConditionKheops exprConditionKheops = new ExprConditionKheops(DicomObject.newDicomObject());
        ExpressionResult.get(condition, exprConditionKheops, Boolean.class);

    }

    /*
    public static boolean validateCondition(String condition) {
        if (!condition.contains("tagValueIsPresent") && !condition.contains("tagValueContains") &&
            !condition.contains("tagValueBeginWith") && !condition.contains("tagValueEndWith")) {
            return false;
        }

        String hexTagInCondition = getHexTagInCondition(condition);
        if (!hexTagInCondition.equals("")) {
            return isHexTag(hexTagInCondition);
        }

        String tagObject = getTagObjectInCondition(condition);
        if (!tagObject.equals("")) {
            try {
                new Tag().getClass().getField(tagObject.split("\\.")[1]);
            } catch (Exception e){
                return false;
            }
            return true;
        }
        return false;
    }

    private static String getTagObjectInCondition(String condition) {
        Pattern pattern = Pattern.compile("(Tag\\.[a-zA-Z]+)");
        Matcher matcher = pattern.matcher(condition);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return "";
    }

    private static String getHexTagInCondition(String condition) {
        Pattern pattern = Pattern.compile("((\\()[0-9a-fA-F]{4},?[0-9a-fA-F]{4}(\\)))");
        Matcher matcher = pattern.matcher(condition);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return "";
    }
     */
}