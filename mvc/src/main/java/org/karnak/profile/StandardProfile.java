package org.karnak.profile;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.Tag;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.DReplace;
import org.karnak.profile.action.KKeep;

import java.util.ArrayList;
import java.util.HashMap;

public class StandardProfile implements ProfileChain{
    private ProfileChain parent;
    private HashMap<Integer, Action> tagList;

    public StandardProfile() {
        this.tagList.put(Tag.PatientName, new DReplace());
        this.tagList.put(Tag.PatientSex, new KKeep());
    }

    @Override
    public KeepEnum isKeep(DicomElement dcmElem) {
        return isKeep(dcmElem);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        return this.tagList.get(dcmElem.tag());
    }
}
