package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profilepipe.action.Action;
import org.karnak.profilepipe.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionTags extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(ActionTags.class);
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private Action actionByDefault;

    public ActionTags(String name, String codeName, String action, List<String> tags, List<String> exceptedTags) throws Exception {
        super(name, codeName, action, tags, exceptedTags);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = Action.convertAction(this.action);
        errorManagement();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        for (String tag: tags) {
            tagsAction.put(tag, actionByDefault);
        }
        if (exceptedTags != null) {
            for (String tag : exceptedTags) {
                exceptedTagsAction.put(tag, actionByDefault);
            }
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if (exceptedTagsAction.get(dcmElem.tag()) == null) {
            return tagsAction.get(dcmElem.tag());
        }
        return null;
    }

    private void errorManagement() throws Exception{
        if (action == null && tags == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action and no tags defined");
        }

        if (action == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }

        if (actionByDefault == Action.REPLACE) {
            throw new Exception("Cannot build the profile " + codeName + ": Action D is not recognized in this profile");
        }

        if (tags == null) {
            throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
        }
    }
}
