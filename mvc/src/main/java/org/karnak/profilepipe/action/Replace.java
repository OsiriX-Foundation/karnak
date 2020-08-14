package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class Replace extends AbstractAction {

    public Replace(String symbol) {
        super(symbol);
    }

    public Replace(String symbol, String dummyValue) {
        super(symbol, dummyValue);
    }

    @Override
    public void execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        dcm.get(tag).ifPresent(dcmEl -> {
            if (dummy != null) {
                dcm.setString(tag, dcmEl.vr(), dummy);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });
    }
}
