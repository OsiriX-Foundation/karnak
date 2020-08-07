package org.karnak.standard;

import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.Action;
import org.karnak.profilepipe.profiles.AbstractProfileItem;
import org.karnak.profilepipe.profiles.PrivateTags;
import org.karnak.profilepipe.profiles.ProfileItem;
import org.karnak.profilepipe.utils.PrivateTagPattern;
import org.karnak.profilepipe.utils.TagActionMap;
import org.karnak.standard.dicominnolitics.StandardConfidentialityProfiles;
import org.karnak.standard.dicominnolitics.jsonConfidentialityProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConfidentialityProfiles {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfidentialityProfiles.class);

    private TagActionMap actionMap = new TagActionMap();
    private final List<ProfileItem> listProfiles = new ArrayList<>();

    public ConfidentialityProfiles() {
        final StandardConfidentialityProfiles standardConfidentialityProfiles = new StandardConfidentialityProfiles();
        jsonConfidentialityProfiles[] confidentialityProfiles = standardConfidentialityProfiles.getConfidentialityProfiles();

        for (jsonConfidentialityProfiles confidentialityProfilesTag : confidentialityProfiles) {
            String tag = confidentialityProfilesTag.getTag();
            Action action = confidentialityProfilesTag.getBasicProfile();
            String name = confidentialityProfilesTag.getName();
            AbstractProfileItem item;
            if (PrivateTagPattern.TAG_PATTERN.equals(tag)) {
                try {
                    final ProfileElement profileElement = new ProfileElement(name, AbstractProfileItem.Type.ACTION_PRIVATETAGS.getClassAlias(), "X", null, null);
                    //#TODO item = new PrivateTags(name, tag, "X", null, null);
                    item = new PrivateTags(profileElement);
                } catch (Exception e) {
                    item = null;
                    LOGGER.error("Cannot build the profile: PrivateTags", e);
                }
            } else {
                actionMap.put(tag, action);
                item = null;
            }

            if (item != null) {
                listProfiles.add(item);
            }
        }
    }

    public TagActionMap getActionMap() {
        return actionMap;
    }

    public List<ProfileItem> getListProfiles() {
        return listProfiles;
    }

}
