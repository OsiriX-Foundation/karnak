package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.*;
import org.karnak.profilepipe.utils.HMAC;

public class UpdateUIDsProfile extends AbstractProfileItem {

    public UpdateUIDsProfile(ProfileElement profileElement) {
        super(profileElement);
        /*
        if (not BlackList) {
            throw new IllegalStateException(String.format("The policy %s is not consistent with the profile %s!", policy, codeName));
        }
        */
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        ActionItem val = tagMap.get(dcmElem.tag());
        if (val != null) {
            return val;
        }
        return null;
    }

    @Override
    public ActionItem put(int tag, ActionItem action) {
        if ( !(UID.class.isInstance(action)) && !(Remove.class.isInstance(action)) && !(ReplaceNull.class.isInstance(action))) {
            throw new IllegalStateException(String.format("The action %s is not consistent !", action));
        }
        return tagMap.put(tag, action);
    }
}
