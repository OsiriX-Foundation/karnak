package org.karnak.profileschain.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.dcm4che6.data.ElementDictionary;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.profile.ActionTable;
import org.karnak.data.profile.ProfileTable;
import org.karnak.profileschain.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class JSONparser extends ParserProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONparser.class);

    @Override
    public ProfileTable parse(URL url, String profileName) {
        final ProfileTable profileTable = new ProfileTable(Objects.requireNonNull(profileName));
        HashMap<Integer, Action> actionMap = new HashMap<>();
        try (InputStreamReader inputStream = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            JsonElement jsonTree = JsonParser.parseReader(inputStream);
            for (JsonElement entry : jsonTree.getAsJsonArray()) {
                JsonObject val = entry.getAsJsonObject();
                String tagKey = val.get("id").getAsString();
                String name = val.get("name").getAsString();
                String action = val.get("basicProfile").getAsString();
                try {
                    int intTag = TagUtils.intFromHexString(tagKey);;
                    profileTable.addAction(new ActionTable(profileTable, intTag, action, name));
                    actionMap.put(intTag, convertAction(action));
                } catch (Exception e) {
                    LOGGER.error("Cannot read tag \"{}\" to register in HashMap", name, e);
                }
            }
        } catch (Exception e) {
            throw new JsonParseException("Cannot parse json profile correctly", e);
        }
        return profileTable;
    }
}
