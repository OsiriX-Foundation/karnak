package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import java.util.Objects;

public class PrivateTagsProfile extends AbstractProfileItem {
    public static final String TAG_PATTERN = "ggggeeee-where-gggg-is-odd";

    public PrivateTagsProfile(String name, String codeName, Policy policy, ProfileItem parentProfile) {
        super(name, codeName, policy, parentProfile);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        boolean retainMode = policy == Policy.WHITELIST;
        int tag = dcmElem.tag();
        if (TagUtils.isPrivateGroup(tag)) {
            if(retainMode){
                return tagMap.getOrDefault(tag, Action.REMOVE);
            }
            return tagMap.getOrDefault(tag, Action.KEEP);
        }
        return profileParent == null ? null : getParentAction(dcmElem);
    }
}
