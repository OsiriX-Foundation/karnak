package org.karnak.profileschain.parser;

import org.karnak.data.profile.ProfileTable;
import org.karnak.profileschain.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public abstract class ParserProfile {

    abstract public ProfileTable parse(URL url, String profileName);

    public static Action convertAction(String strAction) {
        return switch (strAction) {
            case "D", "C", "Z/D", "X/D", "X/Z/D" -> Action.DEFAULT_DUMMY;
            case "Z", "X/Z" -> Action.REPLACE_NULL;
            case "X" -> Action.REMOVE;
            case "K" -> Action.KEEP;
            case "U", "X/Z/U" -> Action.UID;
            default -> Action.REPLACE;
        };
    }
}
