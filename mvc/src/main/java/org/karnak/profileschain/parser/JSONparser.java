package org.karnak.profileschain.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.profile.*;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.profiles.PrivateTagsProfile;
import org.karnak.profileschain.profiles.TagPatternProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JSONparser extends ParserProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONparser.class);

    @Override
    public ProfileTable parse(URL url, String profileName) {
        final ProfileTable profileTable = new ProfileTable(Objects.requireNonNull(profileName));
        try (InputStreamReader inputStream = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            JsonElement jsonTree = JsonParser.parseReader(inputStream);
            for (JsonElement entry : jsonTree.getAsJsonArray()) {
                JsonObject val = entry.getAsJsonObject();
                String tagKey = val.get("id").getAsString();
                String name = val.get("name").getAsString();
                String action = val.get("basicProfile").getAsString();
                try {
                    int intTag = TagUtils.intFromHexString(tagKey);
                    profileTable.addAction(new TagActionTable(profileTable, intTag, action, name));
                } catch (Exception e) {
                    if (!handleTagGroup(profileTable, tagKey, name)) {
                        LOGGER.error("Cannot read tag \"{}\" to register in HashMap", name, e);
                    }
                }
            }
        } catch (Exception e) {
            throw new JsonParseException("Cannot parse json profile correctly", e);
        }
        return profileTable;
    }

    private boolean handleTagGroup(ProfileTable profileTable, String tagKey, String name) {
        if (PrivateTagsProfile.TAG_PATTERN.equals(tagKey) || TagPatternProfile.isValid(tagKey)) {
            GroupTag group = new GroupTag(profileTable, name, Policy.WHITELIST, tagKey);
//            if(PrivateTagsProfile.TAG_PATTERN.equals(tagKey) ){ //
//                group.getTagActions().add(new GroupTagAction(group, TagUtils.intFromHexString("00290010"), Action.KEEP.getSymbol() , ""));
//                group.getTagActions().add(new GroupTagAction(group, TagUtils.intFromHexString("00511008"), Action.KEEP.getSymbol() , ""));
//            }
            profileTable.addGroupAction(group);
        } else {
            return false;
        }
        return true;
    }
}
