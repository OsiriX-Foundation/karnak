package org.karnak.profileschain.utils;

import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;

import java.util.HashMap;
import java.util.Map;

public class TagActionMap {
    private HashMap<Integer, Action> tagAction;
    private HashMap<String, Action> tagPatternAction;

    public TagActionMap() {
        tagAction = new HashMap<>();
        tagPatternAction = new HashMap<>();
    }

    public void put(String tag, Action action) {
        String cleanTag = tag.replaceAll("[(),]", "");
        if (tag.contains("X")) {
            tagPatternAction.put(cleanTag, action);
        } else {
            tagAction.put(TagUtils.intFromHexString(cleanTag), action);
        }
    }

    public Action get(Integer tag) {
        Action action = tagAction.get(tag);
        if (action == null) {
            for (Map.Entry<String, Action> entry: tagPatternAction.entrySet()) {
                String currentTagPattern = entry.getKey();
                int patternTag = TagUtils.intFromHexString(currentTagPattern.replace("X", "0"));
                int patternMask = TagUtils.intFromHexString(getMask(currentTagPattern));

                if ((tag & patternMask) == patternTag) {
                    return entry.getValue();
                }
            }
        }
        return action;
    }

    public int size() {
        return this.tagAction.size() + this.tagPatternAction.size();
    }

    public boolean isEmpty() {
        return this.tagAction.isEmpty() && this.tagPatternAction.isEmpty();
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
}
