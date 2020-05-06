package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;

public class ZReplace extends Action {
    private String strAction = "Z";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            dcm.setNull(tag, dcmItem.get().vr());
        }
    }
}
