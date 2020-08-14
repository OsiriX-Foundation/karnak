package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class ReplaceNull extends AbstractAction {


    public ReplaceNull(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        dcm.get(tag).ifPresent(dcmEl -> {
            dcm.setNull(tag, dcmEl.vr());
        });
    }
}
