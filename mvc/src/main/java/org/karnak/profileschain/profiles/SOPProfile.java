package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.KKeep;
import org.karnak.profileschain.action.XRemove;
import org.karnak.profileschain.action.ZReplace;
import org.karnak.profileschain.parser.SOPParser;

import java.io.InputStream;
import java.util.HashMap;

public class SOPProfile implements ProfileChain{
    private String profileName;
    private String args;
    private ProfileChain parent;
    private HashMap<Integer, Integer> sopMap = new HashMap<>();

    public SOPProfile() {
        this.parent = null;
    }

    public SOPProfile(ProfileChain parent) {
        this.parent = parent;
        InputStream inputStream = this.getClass().getResourceAsStream("minSOP_CTImage.json");
        final SOPParser parserProfile = new SOPParser();
        sopMap = parserProfile.parse(inputStream);
    }

    public Integer getType(Integer tag){
        if (sopMap.containsKey(tag)) {
            return sopMap.get(tag);
        }
        return -1;
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        Action action = switch (getType(dcmElem.tag())){
            case 1 -> new KKeep();
            case 2 -> new ZReplace();
            default -> new XRemove();
        };
        return action;
    }
}
