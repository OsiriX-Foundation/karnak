package org.karnak.profilepipe.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.ActionItem;
import org.karnak.profilepipe.utils.TagActionMap;
import org.karnak.standard.ConfidentialityProfiles;

import java.util.List;

public class BasicProfile extends AbstractProfileItem {
    private final List<ProfileItem> listProfiles;
    private final TagActionMap actionMap;

    public BasicProfile(ProfileElement profileElement) {
        super(profileElement);
        ConfidentialityProfiles confidentialityProfiles = AppConfig.getInstance().getConfidentialityProfile();
        actionMap = confidentialityProfiles.getActionMap();
        listProfiles = confidentialityProfiles.getListProfiles();
    }

    @Override
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, String PatientID) {
        int tag = dcmElem.tag();
        ActionItem action = actionMap.get(tag);
        if (action == null) {
            for (ProfileItem p : listProfiles) {
                ActionItem val = p.getAction(dcm, dcmCopy, dcmElem, PatientID);
                if(val != null){
                    return val;
                }
            }
            return null;
        }
        return action;
    }
}
