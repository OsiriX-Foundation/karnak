package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.ActionStrategy;

public interface ProfileItem {
    Action getAction(DicomElement dcmElem);

    String getName();

    String getCodeName();

    ProfileItem getProfileParent();
}
