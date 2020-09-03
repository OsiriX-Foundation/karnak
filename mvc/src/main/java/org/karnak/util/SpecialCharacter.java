package org.karnak.util;

import java.util.regex.Pattern;

public class SpecialCharacter {
    // Response Ferran Maylinch
    // https://stackoverflow.com/questions/10664434/escaping-special-characters-in-java-regular-expressions
    public static String escapeSpecialRegexChars(String str) {
        Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }
}
