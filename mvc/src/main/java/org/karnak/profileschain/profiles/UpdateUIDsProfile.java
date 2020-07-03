package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

public class UpdateUIDsProfile extends AbstractProfileItem {

    public UpdateUIDsProfile(String name, String codeName, ProfileItem parentProfile) {
        this(name, codeName, Type.REPLACE_UID.getPolicy(), parentProfile);
    }

    public UpdateUIDsProfile(String name, String codeName, Policy policy, ProfileItem parentProfile) {
        super(name, codeName, policy, parentProfile);
        if (policy != Type.REPLACE_UID.getPolicy()) {
            throw new IllegalStateException(String.format("The policy %s is not consistent with the profile %s!", policy, codeName));
        }
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        Action val = tagMap.get(dcmElem.tag());
        if (val != null) {
            return val;
        }
        return profileParent == null ? null : getParentAction(dcmElem);
    }

    @Override
    public Action put(int tag, Action action) {
        if (action != Action.UID && action != Action.REMOVE && action != Action.REPLACE_NULL) {
            throw new IllegalStateException(String.format("The action %s is not consistent with the profile policy %s!", action, policy));
        }
        return tagMap.put(tag, action);
    }
}
