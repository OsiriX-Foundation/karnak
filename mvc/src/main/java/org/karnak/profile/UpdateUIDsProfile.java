package org.karnak.profile;

import org.dcm4che6.data.DicomElement;

import org.dcm4che6.data.Tag;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.DReplace;
import org.karnak.profile.action.KKeep;
import org.karnak.profile.action.UUID;

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
    public KeepEnum isKeep(DicomElement dcmElem) {
        return this.parent.isKeep(dcmElem); //ask profile parent
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if(uidTagList.containsKey(dcmElem.tag())){
            return new UUID();
        }else{
            return this.parent.getAction(dcmElem);
        }
    }
}
