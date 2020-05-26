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
        if ((dcmElem.tag() & Tag.OverlayData) != 0) {
            return Action.REMOVE;
        }
        return this.getParentAction(dcmElem);
    }
}
