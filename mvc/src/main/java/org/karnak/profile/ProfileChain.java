package org.karnak.profile;

import org.dcm4che6.data.DicomElement;
import org.karnak.profile.action.Action;

public interface ProfileChain {
    Action getAction(DicomElement dcmElem);
}
