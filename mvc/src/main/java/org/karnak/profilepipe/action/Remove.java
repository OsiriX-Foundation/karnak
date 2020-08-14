package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class Remove implements ActionStrategy {
    @Override
    public Output execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        return null;
    }
}
