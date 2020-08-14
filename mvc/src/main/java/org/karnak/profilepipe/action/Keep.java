package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class Keep extends AbstractAction {

    public Keep(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, String pseudo, String dummy) {

    }
}
