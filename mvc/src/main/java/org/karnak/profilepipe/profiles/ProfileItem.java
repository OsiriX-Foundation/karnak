package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.profile.Argument;
import org.karnak.profilepipe.action.ActionItem;

import java.util.List;

public interface ProfileItem {
    ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, String PatientID);

    ActionItem put(int tag, ActionItem action);

    ActionItem remove(int tag);

    void clearTagMap();

    String getName();

    String getCodeName();

    String getCondition();

    String getOption();

    List<Argument> getArguments();

    Integer getPosition();

    void profileValidation() throws Exception;
}
