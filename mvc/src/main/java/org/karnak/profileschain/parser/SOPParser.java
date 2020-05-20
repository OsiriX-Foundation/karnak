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

public class SOPParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SOPParser.class);

    public SOPParser() {}

    public HashMap<Integer, Integer> parse(InputStream inputStream) {
        HashMap<Integer, Integer> sopMap = new HashMap<>();

        try{
            com.google.gson.JsonArray sopArray = new com.google.gson.JsonArray();
            final JsonElement root = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            sopArray = root.getAsJsonArray();
            try {
                sopArray.forEach(sopRow -> {
                    final JsonObject sopObj = sopRow.getAsJsonObject();
                    final String tag = sopObj.get("tag").getAsString();
                    final String type = sopObj.get("type").getAsString();
                    Integer intTag = 0;
                    try {
                        intTag = TagUtils.intFromHexString(cleanTag(tag));
                    } catch (Exception e) {
                        LOGGER.error("Cannot read tag {} to register in HashMap", tag, e);
                    }
                    sopMap.put(intTag, Integer.parseInt(type));
                });
            } catch (final Exception e) {
                LOGGER.error("Cannot register json profile in HashMap", e);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot parser reader", e);
        }
        return sopMap;
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
}
