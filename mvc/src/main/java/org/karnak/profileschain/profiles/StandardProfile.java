package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.profileschain.action.Action;

import java.util.HashMap;

public class StandardProfile extends AbstractProfileItem {

    private final HashMap<Integer, Action> tagList;

    private BasicDicomProfile basicDicomProfile;{
        basicDicomProfile = AppConfig.getInstance().getStandardProfile();
    }

    public StandardProfile(String name, String codeName, ProfileItem profileParent) {
        super(name, codeName, profileParent);
        this.tagList = basicDicomProfile.getActionMap();
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        int tag = dcmElem.tag();
        Action action = tagList.get(tag);
        if (action == null) {
            if (TagUtils.isPrivateGroup(tag)) {
                return Action.REMOVE;
            }
            return this.getParentAction(dcmElem);
        }
        /*
        else {
            if (dcmElem.vr() == VR.UI && Action.UID.getSymbol().equals(action.getSymbol())) {
                return Action.UID;
            }
        }
        */
        return action;
    }
}
