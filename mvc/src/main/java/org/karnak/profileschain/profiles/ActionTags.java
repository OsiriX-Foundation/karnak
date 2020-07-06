package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionTags extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(ActionTags.class);
    private TagActionMap tagsAction;

    public ActionTags(String name, String codeName, String action, List<String> tags) throws Exception {
        super(name, codeName, action, tags);
        tagsAction = new TagActionMap();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        Action action = stringToAction();

        if (stringToAction() == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }

        if (tags == null || tags.size() == 0) {
            throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
        }

        for (String tag: tags) {
            tagsAction.put(tag, action);
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return tagsAction.get(dcmElem.tag());
    }

    public Action stringToAction() {
        return switch (action) {
            case "REPLACE_NULL" -> Action.REPLACE_NULL;
            case "REMOVE" -> Action.REMOVE;
            case "KEEP" -> Action.KEEP;
            case "REPLACE_UID" -> Action.UID;
            default -> null;
        };
    }
}
