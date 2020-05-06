package org.karnak.profile.parser;

import org.karnak.profile.action.*;
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
}
