package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.ExceptedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.profilepipe.action.Action;

import java.util.List;

public class UpdateUIDsProfile extends AbstractProfileItem {

    public UpdateUIDsProfile(String name, String codeName, String action, List<IncludedTag> tags, List<ExceptedTag> exceptedTags) {
        super(name, codeName, action, tags, exceptedTags);
        /*
        if (not BlackList) {
            throw new IllegalStateException(String.format("The policy %s is not consistent with the profile %s!", policy, codeName));
        }
        */
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        Action val = tagMap.get(dcmElem.tag());
        if (val != null) {
            return val;
        }
        return null;
    }

    @Override
    public Action put(int tag, Action action) {
        if (action != Action.UID && action != Action.REMOVE && action != Action.REPLACE_NULL) {
            throw new IllegalStateException(String.format("The action %s is not consistent !", action));
        }
        return tagMap.put(tag, action);
    }
}
