package org.karnak.backend.model.profiles;

import java.util.List;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.backend.configuration.AppConfig;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.profilepipe.TagActionMap;
import org.karnak.backend.model.standard.ConfidentialityProfiles;
import org.karnak.data.profile.ProfileElement;

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
    public ActionItem getAction(DicomObject dcm, DicomObject dcmCopy, DicomElement dcmElem, HMAC hmac) {
        int tag = dcmElem.tag();
        ActionItem action = actionMap.get(tag);
        if (action == null) {
            for (ProfileItem p : listProfiles) {
                ActionItem val = p.getAction(dcm, dcmCopy, dcmElem, hmac);
                if(val != null){
                    return val;
                }
            }
            return null;
        }
        return action;
    }
}
