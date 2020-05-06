package org.karnak.profile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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
import org.karnak.profile.parser.JSONparser;
import org.karnak.profile.parser.ParserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    private final String standardProfilePath = "profile.json";
    private  HashMap<Integer, Action> actionMap = new HashMap<>();
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

        final Boolean stantardProfileExist = this.profilePersistence.existsByName("standardProfile");

        if (stantardProfileExist) {
            final ProfileTable standardProfileTable = this.profilePersistence.findByName("standardProfile");
            final Set<ActionTable> standardActionTable = standardProfileTable.getActions();
            standardActionTable.forEach(action->{
                register(action.getTag(), action.getAction());
            });
        } else {
            InputStream inputStream = this.getClass().getResourceAsStream(this.standardProfilePath);
            ParserProfile parserProfile = new JSONparser();
            actionMap = parserProfile.parse(inputStream);
            persistProfile(this.actionMap, "standardProfile");
        }
        
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



    public void persistProfile(HashMap<Integer, Action> aMap, String profileName) {
        final ProfileTable profileTable = new ProfileTable(profileName);
        try {
            aMap.forEach((tag, action) -> {
                final ActionTable actionTable = new ActionTable(profileTable, tag, action.getStrAction(), "AttributeName");
                profileTable.addAction(actionTable);
            });
            this.profilePersistence.save(profileTable);
        } catch (final Exception e) {
            LOGGER.error("Cannot persist json profile in database {}", profileName, e);
        }
    }
}