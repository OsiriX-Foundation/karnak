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

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudonym, String dummyValue) {
        dcm.get(tag).ifPresent(dcmEl -> {
            if (dummyValue != null) {
                dcm.setString(tag, dcmEl.vr(), dummyValue);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });
    }
}