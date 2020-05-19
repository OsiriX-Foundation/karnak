package org.karnak.profileschain.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dcm4che6.util.TagUtils;
import org.karnak.profileschain.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JSONparser extends ParserProfile{
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONparser.class);

    @Override
    public HashMap<Integer, Action> parse(InputStream inputStream) {
        com.google.gson.JsonObject rootobj = new com.google.gson.JsonObject();
        HashMap<Integer, Action> actionMap = new HashMap<>();

        try {
            final JsonElement root = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            rootobj = root.getAsJsonObject();

            try {
                for (Map.Entry<String, JsonElement> entry : rootobj.entrySet()) {
                    final JsonObject val = entry.getValue().getAsJsonObject();
                    final String action = val.get("action").getAsString();
                    final String tagKey = entry.getKey();
                    Integer intTag = 0;
                    try {
                        intTag = TagUtils.intFromHexString(cleanTag(tagKey));
                    } catch (Exception e) {
                        LOGGER.error("Cannot read tag {} to register in HashMap", tagKey, e);
                    }
                    actionMap.put(intTag, convertAction(action));
                }
            } catch (final Exception e) {
                LOGGER.error("Cannot register json profile in HashMap", e);
            }

        } catch (Exception e) {
            LOGGER.error("Cannot parser reader", e);
        }

        return actionMap;
    }
}
