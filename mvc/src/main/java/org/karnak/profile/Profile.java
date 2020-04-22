package org.karnak.profile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
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
    private Action xRemove = new XRemove();
    private Action dReplace = new DReplace();
    private Action zReplace = new ZReplace();
    private Action kKeep = new KKeep();
    private Action uUid = new UUID();

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
        readJsonProfile(filename);
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
        for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext();) {
            DicomElement dcmEl = iterator.next();
            Action action = actionMap.get(dcmEl.tag());
            if (action != null) { // if action != keep
                action.execute(dcm, dcmEl.tag(), iterator);
            }
        }
        /*
         * dcm.elementStream().forEach(e -> { Action action = actionMap.get(e.tag()); if(action != null){ //if action !=
         * keep action.execute(dcm, e.tag()); } });
         */
    }

    public void readJsonProfile(String filename) {
        try {
        JsonElement root =
            JsonParser.parseReader(new InputStreamReader(this.getClass().getResourceAsStream("profile.json"), StandardCharsets.UTF_8));
        JsonObject rootobj = root.getAsJsonObject();
        for (Entry<String, JsonElement> entry : rootobj.entrySet()) {
            JsonObject val = entry.getValue().getAsJsonObject();
            String action = val.get("action").getAsString();
            String attributeName = val.get("attributeName").getAsString();

            Integer intTag = hexToDecimal(cleanTag(entry.getKey()));
            // System.out.println("Att.: "+attributeName+"\t\tTag: "+tag+"\t\tDec. val: "+intTag+ "\t\tAction:
            // "+action);

            register(intTag, action);
        }
        } catch (Exception e) {
            LOGGER.error("Cannot parse profile", e);
        }
    }

    public static int hexToDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int decimal = 0;
        try {

            for (int i = 0; i < hex.length(); i++) {
                char c = hex.charAt(i);
                int d = digits.indexOf(c);
                decimal = 16 * decimal + d;
            }
            return decimal;
        } catch (NumberFormatException e) { // handle your exception
            e.printStackTrace();
        }
        return decimal;

    }

    public String cleanTag(String tag) {
        try {
            if (tag.contains("(") || tag.contains(")") || tag.contains(",")) {
                return tag.replace("(", "").replace(")", "").replace(",", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }
}