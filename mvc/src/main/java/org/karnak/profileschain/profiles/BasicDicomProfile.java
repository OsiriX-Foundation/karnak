package org.karnak.profileschain.profiles;

import org.karnak.data.AppConfig;
import org.karnak.data.profile.GroupTagAction;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.data.profile.ProfileTable;
import org.karnak.data.profile.TagActionTable;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.parser.JSONparser;
import org.karnak.profileschain.parser.ParserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class BasicDicomProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDicomProfile.class);

    private static final String STD_PROFILE_PATH = "confidentiality_profile_attributes.json";
    private static final String STD_PROFILE_NAME = "standardProfile";
    private final HashMap<Integer, Action> actionMap = new HashMap<>();
    private final List<ProfileItem> groupList = new ArrayList<>();
    private final String profileName;
    private final Long profileID;

    public BasicDicomProfile() {
        ProfilePersistence profilePersistence = AppConfig.getInstance().getProfilePersistence();
        final Boolean standardProfileExist = profilePersistence.existsByName(STD_PROFILE_NAME);
        ProfileTable standardProfileTable;
        if (standardProfileExist) {
            standardProfileTable = profilePersistence.findByName(STD_PROFILE_NAME);
            readActions(standardProfileTable);
        } else {
            URL url = this.getClass().getResource(STD_PROFILE_PATH);
            ProfileTable profileTable = readProfile(url, STD_PROFILE_NAME);
            profilePersistence.save(profileTable);
            standardProfileTable = profilePersistence.findByName(STD_PROFILE_NAME);
        }
        this.profileName = STD_PROFILE_NAME;
        this.profileID = standardProfileTable.getId();
    }

    protected ProfileTable readProfile(URL url, String profileName){
        final ParserProfile parserProfile = new JSONparser();
        ProfileTable profileTable = parserProfile.parse(url, profileName);
        readActions(profileTable);
        return profileTable;
    }

    protected void readActions(ProfileTable profileTable){
        profileTable.getActions().forEach(action -> actionMap.put(action.getTag(), ParserProfile.convertAction(action.getAction())));
        profileTable.getGroupTagSet().forEach(g -> {
            String tagKey = g.getTagPattern();
            String name = g.getGroupName();
            AbstractProfileItem item;
            if (PrivateTagsProfile.TAG_PATTERN.equals(tagKey)) {
                item = new PrivateTagsProfile(name, tagKey, null);
                for(GroupTagAction t : g.getTagActions()) {
                    item.put(t.getTag(),  ParserProfile.convertAction(t.getAction()));
                }
            } else if (TagPatternProfile.isValid(tagKey)) {
                try {
                    item = new TagPatternProfile(name, tagKey, null);
                    for(GroupTagAction t : g.getTagActions()) {
                        item.put(t.getTag(),  ParserProfile.convertAction(t.getAction()));
                    }
                } catch (IllegalArgumentException e) {
                    item = null;
                    LOGGER.error("Tag pattern \"{}\" is not valid", tagKey);
                }
            } else {
                item = null;
                LOGGER.error("Tag pattern \"{}\" not implemented", tagKey);
            }
            if (item != null) {
                this.groupList.add(item);
            }
        });
    }

    public String getProfileName() {
        return profileName;
    }

    public Long getProfileID() {
        return profileID;
    }

    public Map<Integer, Action> getActionMap() {
        return actionMap;
    }

    public List<ProfileItem> getGroupList() {
        return groupList;
    }
}