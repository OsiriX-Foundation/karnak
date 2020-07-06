package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;

import java.util.List;

public class ActionTags extends AbstractProfileItem {
    public ActionTags(String name, String codeName, String action, List<String> tags) {
        super(name, codeName, action, tags);
        setActionHashMap();
    }

    private void setActionHashMap() {
        Action action = stringToAction();
        for (String tag: tags) {
            int tagInt = TagUtils.intFromHexString(tag.replaceAll("[(),]", ""));
            this.put(tagInt, action);
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return this.tagMap.get(dcmElem.tag());
    }

    public Action stringToAction() {
        return switch (action) {
            case "REPLACE_NULL" -> Action.REPLACE_NULL;
            case "REMOVE" -> Action.REMOVE;
            case "KEEP" -> Action.KEEP;
            case "REPLACE_UID" -> Action.UID;
            case "REPLACE" -> Action.REPLACE;
            default -> null;
        };
    }
}
