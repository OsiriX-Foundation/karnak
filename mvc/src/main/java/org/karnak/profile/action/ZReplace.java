package org.karnak.profile.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;

public class ZReplace extends Action {
    private String strAction = "Z";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String value) {
        dcm.get(tag).ifPresent(dcmEl -> {
            dcm.setNull(tag, dcmEl.vr());
        });
    }
}
