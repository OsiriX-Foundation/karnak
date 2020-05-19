package org.karnak.profile;

import org.dcm4che6.data.DicomElement;

import org.dcm4che6.data.Tag;
import org.karnak.profile.action.*;

import java.util.HashMap;

public class UpdateUIDsProfile implements ProfileChain{
    private ProfileChain parent;
    private HashMap<Integer, Action> uidTagList = new HashMap<>();


    public UpdateUIDsProfile(){
        this.parent = new SOPProfile();
    }

    public UpdateUIDsProfile(ProfileChain parent) {
        this.parent = parent;
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if (uidTagList.containsKey(dcmElem.tag())) {
            return new UUID();
        } else if (this.parent != null) {
            return this.parent.getAction(dcmElem);
        } else {
            return new XRemove();
        }
    }
}
