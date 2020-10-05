package org.karnak.profilepipe.action;

import java.util.Iterator;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

public class CleanPixelData extends AbstractAction {

    public CleanPixelData(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID) {
        // Action is executed elsewhere, only used for having a specific action.
    }
}
