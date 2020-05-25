package org.karnak.profileschain.parser;

import org.karnak.data.profile.ProfileTable;
import org.karnak.profileschain.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public abstract class ParserProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserProfile.class);

    abstract public ProfileTable parse(URL url, String profileName);

    public static Action convertAction(String strAction) {
        Action action = switch (strAction) {
            case "D", "C", "Z/D", "X/D", "X/Z/D" -> new DefaultDummyValue();
            case "Z", "X/Z" -> new ZReplace();
            case "X" -> new XRemove();
            case "K" -> new KKeep();
            case "U", "X/Z/U" -> new UUID();
            default -> new DReplace();
        };
        return action;
    }
}
