package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.AbstractAction;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.utils.TagActionMap;

public class ActionTags extends AbstractProfileItem {
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private ActionItem actionByDefault;

    public ActionTags(ProfileElement profileElement) throws Exception {
        super(profileElement);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = AbstractAction.convertAction(this.action);
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
    public ActionItem getAction(DicomObject dcmCopy, DicomElement dcmElem) {
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

        if (tags == null || tags.size() == 0) {
            throw new Exception("Cannot build the profile " + codeName + ": No tags defined");
        }
    }
}
