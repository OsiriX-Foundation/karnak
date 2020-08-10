package org.karnak.profilepipe.utils;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.weasis.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyDCMElem {

    private int tag;
    private VR vr;
    private String stringValue;

    public MyDCMElem(int tag, VR vr, DicomObject dcm){
        this.tag = Objects.requireNonNull(tag);
        this.vr = Objects.requireNonNull(vr);
        this.stringValue = dcm.getString(this.tag).orElse(null);
    }

    public MyDCMElem(int tag, VR vr, String stringValue){
        this.tag = Objects.requireNonNull(tag);
        this.vr = Objects.requireNonNull(vr);
        this.stringValue = stringValue;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public VR getVr() {
        return vr;
    }

    public void setVr(VR vr) {
        this.vr = vr;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String conditionInterpreter(String condition){
        String[] conditionArray;

        conditionArray = condition.split(" ");

        List<String> newConditionList = Arrays.stream(conditionArray).map( elem -> {

            if (isHexTag(elem)) {
                String cleanTag = elem.replaceAll("[(),]", "").toUpperCase();
                if ( (TagActionMap.isValidPattern(cleanTag))) {
                    String currentTagPattern = cleanTag;
                    int patternTag = TagUtils.intFromHexString(currentTagPattern.replace("X", "0"));
                    int patternMask = TagUtils.intFromHexString(TagActionMap.getMask(currentTagPattern));

                    if ((tag & patternMask) == patternTag) {
                        return String.valueOf(tag);
                    } else {
                        return "null";
                    }
                }else{
                    return String.valueOf(TagUtils.intFromHexString(cleanTag));
                }

            } else {
                return elem;
            }

        }).collect(Collectors.toList());
        final String delim = " ";
        return String.join(delim, newConditionList);
    }

    public static boolean isHexTag(String elem){
        String cleanElem = elem.replaceAll("[(),]", "").toUpperCase();

        if (!StringUtil.hasText(cleanElem) || cleanElem.length() != 8) {
            return false;
        }
        return cleanElem.matches("[0-9A-FX]+");
    }
}
