package org.karnak.profilepipe.utils;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.profilepipe.action.*;
import org.weasis.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExprDCMElem {

    private int tag;
    private VR vr;
    private String stringValue;
    private DicomObject dcm;
    private DicomObject dcmCopy;

    public ExprDCMElem(int tag, VR vr, DicomObject dcm, DicomObject dcmCopy){
        this.tag = Objects.requireNonNull(tag);
        this.vr = Objects.requireNonNull(vr);
        this.stringValue = dcmCopy.getString(this.tag).orElse(null);
        this.dcmCopy = dcmCopy;
        this.dcm = dcm;
    }

    public ExprDCMElem(int tag, VR vr, String stringValue){
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

    public ActionItem Keep(){
        return new Keep("K");
    }

    public ActionItem Remove(){
        return new Remove("X");
    }

    public ActionItem Replace(String dummyValue){
        ActionItem replace = new Replace("D");
        replace.setDummyValue(dummyValue);
        return replace;
    }

    public ActionItem UID(){
        return new UID("U");
    }

    public ActionItem ReplaceNull(){
        return new ReplaceNull("Z");
    }

    public ActionItem Add(int newTag, VR newVr, String newValue){
        Add add = new Add("A", newTag, newVr, newValue);
        add.execute(dcm, newTag, null, null);
        return null;
    }

    public String getString(int tag){
        return dcmCopy.getString(tag).orElse(null);
    }

    // #TODO doesn't work in sequence
    public boolean tagIsPresent(int tag){
        return DicomObjectTools.tagIsInDicomObject(tag, dcmCopy);
    }
}
