package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.profileschain.action.Action;

import java.util.List;

public class ActionTags extends AbstractProfileItem {
    public ActionTags(String name, String codeName, ProfileItem parentProfile, String action, List<String> tags) {
        super(name, codeName, parentProfile, action, tags);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        System.out.println(action);
        int currentDCMTag = dcmElem.tag();
        for (String tag: tags) {
            System.out.println(tag);
        }
        return Action.KEEP;
    }
}
