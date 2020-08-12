package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profilepipe.action.Action;

public interface ProfileItem {
    Action getAction(DicomElement dcmElem);

    Action put(int tag, Action action);

    Action remove(int tag);

    void clearTagMap();

    String getName();

    String getCodeName();

    String getCondition();

    String getOption();

    String getArgs();

    Integer getPosition();
}
