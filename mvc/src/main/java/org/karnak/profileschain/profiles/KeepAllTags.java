package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import java.util.List;

public class KeepAllTags extends AbstractProfileItem {

    public KeepAllTags(String name, String codeName, String action, List<String> tags) {
        super(name, codeName, action, tags);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return Action.KEEP;
    }
}
