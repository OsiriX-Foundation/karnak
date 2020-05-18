package org.karnak.profile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.dcm4che6.data.ElementDictionary;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ActionTable;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.data.profile.ProfileTable;
import org.karnak.profile.action.*;
import org.karnak.profile.parser.JSONparser;
import org.karnak.profile.parser.ParserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    private final String standardProfilePath = "profile.json";
    private final String standardProfileName = "standardProfile";
    private  HashMap<Integer, Action> actionMap = new HashMap<>();
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

    private ProfilePersistence profilePersistence;{
        profilePersistence = AppConfig.getInstance().getProfilePersistence();
    }

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
            InputStream inputStream = this.getClass().getResourceAsStream(this.standardProfilePath);
            final ParserProfile parserProfile = new JSONparser();
            this.actionMap = parserProfile.parse(inputStream);
            persistProfile(this.actionMap, this.standardProfileName);
            standardProfileTable = this.profilePersistence.findByName(this.standardProfileName);
        }
        this.profileName = this.standardProfileName;
        this.profileID = standardProfileTable.getId();
    }

    public void persistProfile(HashMap<Integer, Action> aMap, String profileName) {
        final ProfileTable profileTable = new ProfileTable(profileName);
        try {
            aMap.forEach((tag, action) -> {
                final ActionTable actionTable = new ActionTable(profileTable, tag, action.getStrAction(), ElementDictionary.keywordOf(tag, Optional.ofNullable("Null")));
                profileTable.addAction(actionTable);
            });
            this.profilePersistence.save(profileTable);
        } catch (final Exception e) {
            LOGGER.error("Cannot persist json profile in database {}", profileName, e);
        }
    }
}