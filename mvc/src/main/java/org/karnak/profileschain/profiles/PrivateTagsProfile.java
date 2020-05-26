package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;

public class PrivateTagsProfile extends AbstractProfileItem {

    public PrivateTagsProfile(String name, String codeName, ProfileItem parentProfile) {
        super(name, codeName, parentProfile);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if (TagUtils.isPrivateGroup(dcmElem.tag())) {
            return Action.REMOVE;
        }
        return this.getParentAction(dcmElem);
    }
}
