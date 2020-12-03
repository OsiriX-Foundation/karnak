package org.karnak.standard.dicominnolitics;

import org.karnak.profilepipe.action.*;

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

    public ActionItem getBasicProfile() {
        return convertAction(basicProfile);
    }

    public ActionItem getStdCompIOD() {
        return convertAction(stdCompIOD);
    }

    public ActionItem getCleanDescOpt() {
        return convertAction(cleanDescOpt);
    }

    private static ActionItem convertAction(String strAction) {
        return switch (strAction) {
            case "D" -> new DefaultDummy("DDum");
            case "Z" -> new ReplaceNull("Z");
            case "X" -> new Remove("X");
            case "K" -> new Keep("K");
            case "U" -> new UID("U");
            case "Z/D", "X/D", "X/Z/D", "X/Z", "X/Z/U", "X/Z/U*" -> new MultipleActions(strAction);
            default -> new Replace("D");
        };
    }
}
