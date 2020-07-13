package org.karnak.standard.dicominnolitics;

import org.karnak.profilepipe.action.Action;

public class jsonConfidentialityProfiles {
    private String id;
    private String name;
    private String tag;
    private String basicProfile;
    private String stdCompIOD;
    private String cleanDescOpt;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Action getBasicProfile() {
        return convertAction(basicProfile);
    }

    public Action getStdCompIOD() {
        return convertAction(stdCompIOD);
    }

    public Action getCleanDescOpt() {
        return convertAction(cleanDescOpt);
    }

    private static Action convertAction(String strAction) {
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
