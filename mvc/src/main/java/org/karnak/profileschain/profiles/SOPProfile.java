package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.parser.SOPParser;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SOPProfile extends AbstractProfileItem {
    private final HashMap<Integer, Integer> sopMap;

    public SOPProfile(String name, String codeName, String action, List<String> tags) {
        super(name, codeName, action, tags);
        final SOPParser parserProfile = new SOPParser();
        URL url = this.getClass().getResource("minSOP_CTImage.json");
        this.sopMap = parserProfile.parse(url);
    }

    public Integer getType(Integer tag) {
        if (sopMap.containsKey(tag)) {
            return sopMap.get(tag);
        }
        return -1;
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return switch (getType(dcmElem.tag())) {
            case 1 -> Action.KEEP;
            case 2 -> Action.REPLACE_NULL;
            default -> Action.REMOVE;
        };
    }
}
