package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class UID implements ActionStrategy {
    @Override
    public Output execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        return null;
    }
}
