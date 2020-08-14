package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.profilepipe.action.ActionItem;

public interface ProfileItem {
    ActionItem getAction(DicomObject dcmCopy, DicomElement dcmElem);

    ActionItem put(int tag, ActionItem action);

    ActionItem remove(int tag);

    void clearTagMap();

    String getName();

    String getCodeName();

    String getCondition();

    String getOption();

    String getArgs();

    Integer getPosition();
}
