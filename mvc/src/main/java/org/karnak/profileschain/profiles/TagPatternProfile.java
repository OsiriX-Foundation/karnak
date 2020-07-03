package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;
import org.weasis.core.util.StringUtil;

import java.util.List;
import java.util.Objects;

public class TagPatternProfile extends AbstractProfileItem {
    private final int patternTag;
    private final int patternMask;

    public TagPatternProfile(String name, String tagPattern, ProfileItem parentProfile, String action, List<String> tags) {
        super(name, Objects.requireNonNull(tagPattern).toUpperCase(), parentProfile, action, tags);
        if (!isValid(getCodeName())) throw new IllegalArgumentException("Not a valid tag pattern");
        this.patternTag = TagUtils.intFromHexString(getCodeName().replace("X", "0"));
        this.patternMask = TagUtils.intFromHexString(getMask(getCodeName()));
    }

    private static String getMask(String tagPattern) {
        char[] chars = tagPattern.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 'X') {
                chars[i] = '0';
            } else {
                chars[i] = 'F';
            }
        }
        return new String(chars);
    }

    public static boolean isValid(String tagPattern) {
        if (!StringUtil.hasText(tagPattern) || tagPattern.length() != 8) {
            return false;
        }
        String p = tagPattern.toUpperCase();
        return p.matches("[0-9A-FX]+") && p.contains("X");
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        int tag = dcmElem.tag();
        // boolean retainMode = policy == Policy.WHITELIST;
        boolean retainMode = false;
        if ((tag & patternMask) == patternTag) {
            if(retainMode){
                return tagMap.getOrDefault(tag, Action.REMOVE);
            }
            return tagMap.getOrDefault(tag, Action.KEEP);
        }
        return profileParent == null ? null : getParentAction(dcmElem);
    }
}
