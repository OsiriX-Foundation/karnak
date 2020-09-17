package org.karnak.kheops;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.weasis.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExprConditionKheops {
    private DicomObject dcm;

    public ExprConditionKheops(DicomObject dcm) {
        this.dcm = dcm;
    }

    public boolean tagValueIsPresent(int tag, String value) {
        String dcmValue = dcm.getString(tag).orElse(null);
        return dcmValue != null ? dcmValue.equals(value) : false;
    }


    public String conditionInterpreter(String condition) {
        String[] conditionArray = condition.split(" ");

        List<String> newConditionList = Arrays.stream(conditionArray).map( elem -> {
            if (isHexTag(elem)) {
                String cleanTag = elem.replaceAll("[(),]", "").toUpperCase();
                return String.valueOf(TagUtils.intFromHexString(cleanTag));
            } else {
                return elem;
            }
        }).collect(Collectors.toList());
        return String.join(" ", newConditionList);
    }

    public static boolean isHexTag(String elem){
        String cleanElem = elem.replaceAll("[(),]", "").toUpperCase();

        if (!StringUtil.hasText(cleanElem) || cleanElem.length() != 8) {
            return false;
        }
        return cleanElem.matches("[0-9A-FX]+");
    }
}
