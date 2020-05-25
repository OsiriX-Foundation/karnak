package org.karnak.profileschain.profiles;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.dcm4che6.data.ElementDictionary;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ActionTable;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.data.profile.ProfileTable;
import org.karnak.profileschain.action.*;
import org.karnak.profileschain.parser.JSONparser;
import org.karnak.profileschain.parser.ParserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    private final String standardProfilePath = "confidentiality_profile_attributes.json";
    private final String standardProfileName = "standardProfile";
    private final HashMap<Integer, Action> actionMap = new HashMap<>();
    private String profileName;
    private Long profileID;

    public String getProfileName() {
        return profileName;
    }

    public Long getProfileID() {
        return profileID;
    }

    public HashMap<Integer, Action> getActionMap() {
        return actionMap;
    }

    private ProfilePersistence profilePersistence = AppConfig.getInstance().getProfilePersistence();

    public Profile() {
        final Boolean standardProfileExist = this.profilePersistence.existsByName(this.standardProfileName);
        ProfileTable standardProfileTable;
        if (standardProfileExist) {
            standardProfileTable = this.profilePersistence.findByName(this.standardProfileName);
            final Set<ActionTable> standardActionTable = standardProfileTable.getActions();
            standardActionTable.forEach(action->{
                this.actionMap.put(action.getTag(), ParserProfile.convertAction(action.getAction()));
            });
        } else {
            final ParserProfile parserProfile = new JSONparser();
            URL url = this.getClass().getResource(this.standardProfilePath);
            ProfileTable profileTable = parserProfile.parse(url, standardProfileName);
            profileTable.getActions().forEach(action->{
                this.actionMap.put(action.getTag(), ParserProfile.convertAction(action.getAction()));
            });
            this.profilePersistence.save(profileTable);
            standardProfileTable = this.profilePersistence.findByName(this.standardProfileName);
        }
        this.profileName = this.standardProfileName;
        this.profileID = standardProfileTable.getId();
    }
}