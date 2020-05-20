package org.karnak.profileschain.parser;

import org.karnak.profileschain.action.*;
import org.karnak.profileschain.action.DefaultDummyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;


public abstract class ParserProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserProfile.class);

    abstract public HashMap<Integer, Action> parse(InputStream inputStream);

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
