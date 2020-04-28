package org.karnak.profile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.data.gateway.ActionTable;
import org.karnak.data.gateway.ProfilePersistence;
import org.karnak.data.gateway.ProfileTable;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.DReplace;
import org.karnak.profile.action.KKeep;
import org.karnak.profile.action.UUID;
import org.karnak.profile.action.XRemove;
import org.karnak.profile.action.ZReplace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    private final HashMap<Integer, Action> actionMap = new HashMap<>();
    private final Action xRemove = new XRemove();
    private final Action dReplace = new DReplace();
    private final Action zReplace = new ZReplace();
    private final Action kKeep = new KKeep();
    private final Action uUid = new UUID();

    private ProfilePersistence profilePersistence;
    {
        profilePersistence = AppConfig.getInstance().getProfilePersistence();
    }

    public Profile() {
        register(Tag.StudyID, dReplace);
        register(Tag.StudyDescription, xRemove);
        register(Tag.SOPInstanceUID, uUid);
        register(Tag.SeriesInstanceUID, uUid);
        register(Tag.StudyInstanceUID, uUid);
        register(Tag.StudyDate, zReplace);
        register(Tag.PatientName, kKeep);
    }

    public Profile(String filename) {
        final JsonObject jsonProfile = readJsonFile(filename);
        persistJsonProfile(jsonProfile , "standard_profile");
        registerJsonProfile(jsonProfile);
    }

    public void register(Integer tag, Action action) {
        actionMap.put(tag, action);
    }

    public void register(Integer tag, String action) {
        switch (action) {
            case "D":
                register(tag, dReplace);
                break;
            case "Z":
                register(tag, zReplace);
                break;
            case "X":
                register(tag, xRemove);
                break;
            case "K":
                register(tag, kKeep);
                break;
            case "C":
                register(tag, dReplace); // waiting clean implement.
                break;
            case "U":
                register(tag, uUid);
                break;
            case "Z/D":
                register(tag, dReplace);
                break;
            case "X/Z":
                register(tag, zReplace);
                break;
            case "X/D":
                register(tag, dReplace);
                break;
            case "X/Z/D":
                register(tag, dReplace);
                break;
            case "X/Z/U":
                register(tag, zReplace);
                break;

            default:
                register(tag, dReplace);
                break;
        }
    }

    public void execute(DicomObject dcm) {
        try {
            for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
                final DicomElement dcmEl = iterator.next();
                final Action action = actionMap.get(dcmEl.tag());
                if (action != null) { // if action != keep
                    action.execute(dcm, dcmEl.tag(), iterator);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot execute actions", e);
        }
        /*
         * dcm.elementStream().forEach(e -> { Action action = actionMap.get(e.tag()); if(action != null){ //if action !=
         * keep action.execute(dcm, e.tag()); } });
         */
    }


    public String cleanTag(String tag) {
        try {
            if (tag.contains("(") || tag.contains(")") || tag.contains(",")) {
                return tag.replace("(", "").replace(")", "").replace(",", "");
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot clean tag {}", tag, e);
        }
        return tag;
    }


    public void registerJsonProfile(JsonObject jsonProfile) {
        try {
            for (Entry<String, JsonElement> entry : jsonProfile.entrySet()) {
                final JsonObject val = entry.getValue().getAsJsonObject();
                final String action = val.get("action").getAsString();
                final String tagKey = entry.getKey();
                Integer intTag = 0;
                try {
                    intTag = TagUtils.intFromHexString(cleanTag(tagKey));
                } catch (Exception e) {
                    LOGGER.error("Cannot read tag {} to register in HashMap", tagKey, e);
                }

                register(intTag, action);
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot register json profile in HashMap", e);
        }
    }


    public void persistJsonProfile(JsonObject jsonProfile, String profileName) {
        try {
            final ProfileTable profileTable = new ProfileTable(profileName);
            for (Entry<String, JsonElement> entry : jsonProfile.entrySet()) {
                final JsonObject val = entry.getValue().getAsJsonObject();
                final String action = val.get("action").getAsString();
                final String attributeName = val.get("attributeName").getAsString();
                final String tagKey = entry.getKey();
                Integer intTag = 0;
                try {
                    
                    intTag = TagUtils.intFromHexString(cleanTag(tagKey));
                } catch (Exception e) {
                    LOGGER.error("Cannot read tag {} to persist", tagKey, e);
                }
                
                final ActionTable actionTable = new ActionTable(profileTable, intTag.longValue(), action, attributeName);
                profileTable.addAction(actionTable);
            }

            this.profilePersistence.save(profileTable);

        } catch (Exception e) {
            LOGGER.error("Cannot persist json profile in database {}", profileName, e);
        }
    }

    public JsonObject readJsonFile(String filename){
        JsonObject rootobj = new JsonObject();
        try {
            final JsonElement root = JsonParser.parseReader(
                new InputStreamReader(this.getClass().getResourceAsStream("profile.json"), StandardCharsets.UTF_8));
            rootobj = root.getAsJsonObject();
        } catch (Exception e) {
            LOGGER.error("Cannot read json profile {}", filename, e);
        }
        return rootobj;
    }
}