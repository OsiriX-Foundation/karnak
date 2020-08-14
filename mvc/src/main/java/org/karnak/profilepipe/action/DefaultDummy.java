package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public class DefaultDummy implements ActionStrategy {
    @Override
    public Output execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        return null;
    }
}
