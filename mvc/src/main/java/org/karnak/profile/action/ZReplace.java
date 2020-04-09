package org.karnak.profile.action;

import java.util.Optional;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;

public class ZReplace implements Action {

    public void execute(DicomObject dcm, int tag) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        if(dcmItem.isPresent()) {
            dcm.setNull(tag, dcmItem.get().vr());
        }
    }
}
