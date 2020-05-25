package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.profileschain.action.Action;

import java.util.HashMap;

public class StandardProfile implements ProfileChain{
    private ProfileChain parent;
    private HashMap<Integer, Action> tagList = new HashMap<>();
    private ProfilePersistence profilePersistence;{
        profilePersistence = AppConfig.getInstance().getProfilePersistence();
    }

    private Profile profile;{
        profile = AppConfig.getInstance().getStandardProfile();
    }

    public StandardProfile() {
        this.parent = new UpdateUIDsProfile();
        this.tagList.put(Tag.PatientName, new DReplace());
        this.tagList.put(Tag.PatientSex, new KKeep());
    }

    public StandardProfile(ProfileChain parent) {
        this.parent = parent;
        this.tagList = this.profile.getActionMap();
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if (tagList.containsKey(dcmElem.tag())) {
            return this.tagList.get(dcmElem.tag());
        } else if (this.parent != null) {
            return this.parent.getAction(dcmElem);
        } else {
            return new XRemove();
        }
    }
}