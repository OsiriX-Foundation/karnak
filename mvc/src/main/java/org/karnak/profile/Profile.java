package org.karnak.profile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.ElementDictionary;
import org.karnak.data.AppConfig;
import org.karnak.data.profile.ActionTable;
import org.karnak.data.profile.ProfilePersistence;
import org.karnak.data.profile.ProfileTable;
import org.karnak.profile.action.*;
import org.karnak.profile.option.dummyvalue.DefaultDummyValue;
import org.karnak.profile.parser.JSONparser;
import org.karnak.profile.parser.ParserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    private final String standardProfilePath = "profile.json";
    private final String standardProfileName = "standardProfile";
    private  HashMap<Integer, Action> actionMap = new HashMap<>();
    private DefaultDummyValue defaultDummyValue = new DefaultDummyValue();
    private String profileName;
    private Long profileID;

    private ProfilePersistence profilePersistence;
    {
        profilePersistence = AppConfig.getInstance().getProfilePersistence();
    }

    public Profile() {
        final Boolean standardProfileExist = this.profilePersistence.existsByName(this.standardProfileName);
        ProfileTable standardProfileTable;
        if (standardProfileExist) {
            standardProfileTable = this.profilePersistence.findByName(this.standardProfileName);
            final Set<ActionTable> standardActionTable = standardProfileTable.getActions();
            standardActionTable.forEach(action->{
                this.actionMap.put(action.getTag(), Action.convertAction(action.getAction()));
            });
        } else {
            InputStream inputStream = this.getClass().getResourceAsStream(this.standardProfilePath);
            ParserProfile parserProfile = new JSONparser();
            this.actionMap = parserProfile.parse(inputStream);
            persistProfile(this.actionMap, this.standardProfileName);
            standardProfileTable = this.profilePersistence.findByName(this.standardProfileName);
        }
        this.profileName = this.standardProfileName;
        this.profileID = standardProfileTable.getId();
    }

    public String getProfileName() {
        return profileName;
    }

    public Long getProfileID() {
        return profileID;
    }

    private String setDummyValue(DicomObject dcm, int tag, String patientID) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            DicomElement dcmEl = dcmItem.get();
            return this.defaultDummyValue.execute(dcmEl.vr(), dcm, tag, patientID);
        }
        return null;
    }

    public void execute(DicomObject dcm, String patientID) {
        try {
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                final DicomElement dcmEl = iterator.next();
                final Action action = actionMap.get(dcmEl.tag());
                String value = null;

                if (action != null) { // if action != keep
                    if (action instanceof DReplace) {
                        value = setDummyValue(dcm, dcmEl.tag(), patientID);
                    }
                    action.execute(dcm, dcmEl.tag(), iterator, value);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot execute actions", e);
        }
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