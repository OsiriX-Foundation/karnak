package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import java.util.Iterator;

import org.dcm4che6.data.DicomElement;
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
