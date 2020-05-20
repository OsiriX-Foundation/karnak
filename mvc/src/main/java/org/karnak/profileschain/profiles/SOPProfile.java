package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.KKeep;
import org.karnak.profileschain.action.XRemove;
import org.karnak.profileschain.action.ZReplace;

public class SOPProfile implements ProfileChain{
    private String profileName;
    private String args;
    private ProfileChain parent;

    public SOPProfile() {
        this.parent = null;
    }

    public SOPProfile(ProfileChain parent) {
        this.parent = parent;
    }

    public Integer getType(Integer tag){
        Integer type = switch (tag) {
            case Tag.Modality, Tag.SOPClassUID -> 1;
            case Tag.PatientName, Tag.PatientBirthDate, Tag.PatientSex -> 2;
            case Tag.StudyDescription -> 3;
            default -> -1;
        };
        if(type==-1){
            System.out.println("No type");
        }
        return type;
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        Action action = switch (getType(dcmElem.tag())){
            case 1 -> new KKeep();
            case 2 -> new ZReplace();
            default -> new XRemove();
        };
        return action;
    }
}
