package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.utils.TagActionMap;

import java.util.List;

public class PrivateTags extends AbstractProfileItem {
    private TagActionMap tagsAction;
    private TagActionMap exceptedTagsAction;

    public PrivateTags(String name, String codeName, String action, List<String> tags, List<String> exceptedTags) throws Exception{
        super(name, codeName, action, tags, exceptedTags);
        tagsAction = new TagActionMap();
        exceptedTagsAction = new TagActionMap();
        setActionHashMap(Action.convertAction(this.action));
    }

    private void setActionHashMap(Action action) throws Exception {
        if(tags != null) {
            for (String tag : tags) {
                tagsAction.put(tag, action);
            }
        }
        if (exceptedTags != null) {
            for (String tag : exceptedTags) {
                exceptedTagsAction.put(tag, action);
            }
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        final Action actionByDefault = Action.convertAction(this.action);
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
}
