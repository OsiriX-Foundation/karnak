package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
import org.dcm4che6.data.DicomElement;

public abstract class Action {
    abstract public void execute(DicomObject attributes, int tag, Iterator<DicomElement> iterator, String value);

    abstract public String getStrAction();

    public static Action convertAction(String strAction) {
        Action action = switch (strAction) {
            case "D" -> new DReplace();
            case "Z" -> new ZReplace();
            case "X" -> new XRemove();
            case "K" -> new KKeep();
            case "C" -> new DReplace();
            case "U" -> new UUID();
            case "Z/D" -> new DReplace();
            case "X/Z" -> new ZReplace();
            case "X/D" -> new DReplace();
            case "X/Z/D" -> new DReplace();
            case "X/Z/U" -> new UUID();
            default -> new DReplace();
        };
        return action;
    }
}
