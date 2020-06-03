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
            //Curve Data
            if((dcmElem.tag() & 0xFF000000) == 0x50000000){
                return Action.REMOVE;
            }
            //Overlay Data
            if((dcmElem.tag() & 0xFF00FFFF) == 0x60003000){
                return Action.REMOVE;
            }
            // Overlay Comments
            if((dcmElem.tag() & 0xFF00FFFF) == 0x60004000){
                return Action.REMOVE;
            }
            return this.getParentAction(dcmElem);
        }
        return action;
    }
}
