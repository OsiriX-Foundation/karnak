package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.profileschain.action.Action;

import java.util.HashMap;

public class StandardProfile extends AbstractProfileItem {

    private final HashMap<Integer, Action> tagList;

    public StandardProfile(String name, String codeName) {
        super(name, codeName);
        this.tagList = AppConfig.getInstance().getStandardProfile().getActionMap();
    }
    
    @Override
    public Action getAction(DicomElement dcmElem) {
        int tag = dcmElem.tag();
        Action action = tagList.get(tag);
        if (action == null) {
            if(TagUtils.isPrivateGroup(tag)){
                return Action.REMOVE;
            }
        } else {
            if (dcmElem.vr() == VR.UI && Action.UID.getSymbol().equals(action.getSymbol())) {
                return Action.UID;
            }
        }
        return action;
    }
}
