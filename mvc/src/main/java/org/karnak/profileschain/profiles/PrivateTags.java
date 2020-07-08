package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.utils.TagActionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PrivateTags extends AbstractProfileItem {
    private final Logger LOGGER = LoggerFactory.getLogger(PrivateTags.class);
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;
    private Action actionByDefault;

    public PrivateTags(String name, String codeName, String action, List<String> tags, List<String> exceptedTags) throws Exception{
        super(name, codeName, action, tags, exceptedTags);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        actionByDefault = Action.convertAction(this.action);
        errorManagement();
        setActionHashMap();
    }

    private void setActionHashMap() throws Exception {

        if(tags != null) {
            for (String tag : tags) {
                tagsAction.put(tag, actionByDefault);
            }
        }
        if (exceptedTags != null) {
            for (String tag : exceptedTags) {
                exceptedTagsAction.put(tag, actionByDefault);
            }
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        final int tag = dcmElem.tag();
        if (TagUtils.isPrivateGroup(tag)) {
            if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty()) {
                return tagsAction.get(tag);
            }

            if (tagsAction.isEmpty() && exceptedTagsAction.isEmpty() == false) {
                if(exceptedTagsAction.get(tag) != null){
                    return null;
                }
            }

            if (tagsAction.isEmpty() == false && exceptedTagsAction.isEmpty() == false) {
                if (exceptedTagsAction.get(dcmElem.tag()) == null) {
                    return tagsAction.get(dcmElem.tag());
                }
                return null;
            }
            return actionByDefault;
        }
        return null;
    }

    private void errorManagement() throws Exception{
        if (action == null) {
            throw new Exception("Cannot build the profile " + codeName + ": Unknown Action");
        }
    }
}
