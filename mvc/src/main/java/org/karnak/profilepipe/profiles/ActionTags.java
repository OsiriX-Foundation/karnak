package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
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

    public ActionTags(String name, String codeName, String action, List<IncludedTag> tags, List<ExcludedTag> excludedTags) throws Exception {
        super(name, codeName, action, tags, excludedTags);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = Action.convertAction(this.action);
        errorManagement();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {
        for (IncludedTag tag: tags) {
            tagsAction.put(tag.getTagValue(), actionByDefault);
        }
        if (excludedTags != null) {
            for (ExcludedTag tag : excludedTags) {
                exceptedTagsAction.put(tag.getTagValue(), actionByDefault);
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
        if (action == null && (tags == null || tags.size() > 0)) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action and no tags defined");
        }

        if (action == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }

        if (actionByDefault == Action.REPLACE) {
            throw new Exception("Cannot build the profile " + codeName + ": Action D is not recognized in this profile");
        }

        if (tags == null || tags.size() == 0) {
            throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
        }
    }
}
