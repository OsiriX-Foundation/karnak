package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.Argument;
import org.karnak.profilepipe.action.Action;

import java.util.List;

public interface ProfileItem {
    Action getAction(DicomObject dcmCopy, DicomElement dcmElem);

    Action put(int tag, Action action);

    Action remove(int tag);

    void clearTagMap();

    String getName();

    String getCodeName();

    String getCondition();

    String getOption();

    List<Argument> getArguments();

    Integer getPosition();
}
