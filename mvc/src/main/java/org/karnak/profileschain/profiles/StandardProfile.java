package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
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
            if ((dcmElem.tag() & Tag.OverlayData) != 0) {
                return Action.REMOVE;
            }
            if ((dcmElem.tag() & Tag.OverlayComments) != 0) {
                return Action.REMOVE;
            }
            return this.getParentAction(dcmElem);
        }
        return action;
    }
}
