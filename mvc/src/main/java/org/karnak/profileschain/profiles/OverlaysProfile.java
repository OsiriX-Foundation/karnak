package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;

public class OverlaysProfile extends AbstractProfileItem {

    public OverlaysProfile(String name, String codeName, ProfileItem parentProfile) {
        super(name, codeName, parentProfile);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        // TODO implement all required tags
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
}
