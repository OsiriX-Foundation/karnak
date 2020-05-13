package org.karnak.profile.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import java.util.Iterator;
import org.karnak.profile.option.dummyvalue.DefaultDummyValue;

public class DReplace implements Action{

    private DefaultDummyValue algo;
    private String strAction = "D";

    public DReplace() {
        this.algo = new DefaultDummyValue();
    }

    public DReplace(DefaultDummyValue algo) {
        this.algo = algo;
    }

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String value) {
        dcm.get(tag).ifPresent(dcmEl -> {
            if (value != null) {
                dcm.setString(tag, dcmEl.vr(), value);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });
    }
}