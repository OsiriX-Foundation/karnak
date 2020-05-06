package org.karnak.profile.parser;

import org.karnak.profile.action.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;


public abstract class ParserProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserProfile.class);

    private final Action xRemove = new XRemove();
    private final Action dReplace = new DReplace();
    private final Action zReplace = new ZReplace();
    private final Action kKeep = new KKeep();
    private final Action uUid = new UUID();

    abstract public HashMap<Integer, Action> parse(InputStream inputStream);

    public HashMap<Integer, Action> putActionString(Integer tag, String action, HashMap<Integer, Action> actionMap) {
        switch (action) {
            case "D":
                actionMap.put(tag, dReplace);
                break;
            case "Z":
                actionMap.put(tag, zReplace);
                break;
            case "X":
                actionMap.put(tag, xRemove);
                break;
            case "K":
                actionMap.put(tag, kKeep);
                break;
            case "C":
                actionMap.put(tag, dReplace); // waiting clean implement.
                break;
            case "U":
                actionMap.put(tag, uUid);
                break;
            case "Z/D":
                actionMap.put(tag, dReplace);
                break;
            case "X/Z":
                actionMap.put(tag, zReplace);
                break;
            case "X/D":
                actionMap.put(tag, dReplace);
                break;
            case "X/Z/D":
                actionMap.put(tag, dReplace);
                break;
            case "X/Z/U":
                actionMap.put(tag, zReplace);
                break;
            default:
                actionMap.put(tag, dReplace);
                break;
        }
        return actionMap;
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
