package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profileschain.action.Action;

public interface ProfileItem {
    Action getAction(DicomElement dcmElem);

    String getName();

    String getCodeName();
}
