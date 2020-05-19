package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profileschain.action.Action;

public interface ProfileChain {
    Action getAction(DicomElement dcmElem);
}
