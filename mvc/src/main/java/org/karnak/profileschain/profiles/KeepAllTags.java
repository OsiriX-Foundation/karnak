package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import java.util.List;

public class KeepAllTags extends AbstractProfileItem {

    public KeepAllTags(String name, String codeName, String action, List<String> tags, List<String> exceptedTags) {
        super(name, codeName, action, tags, exceptedTags);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return Action.KEEP;
    }
}
