package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;

import java.util.Iterator;

public class Add extends AbstractAction{
    public Add(String symbol) {
        super(symbol);
    }

    public Add(String symbol, String dummyValue, VR vr) {
        super(symbol, dummyValue, vr);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID) {
        final String tagValueIn = dcm.getString(tag).orElse(null);

        dcm.get(tag).ifPresentOrElse(dcmEl -> {
            if (dummyValue != null) {
                dcm.setString(tag, dcmEl.vr(), dummyValue);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        }, () -> {
            if (dummyValue != null) {
                dcm.setString(tag, vr, dummyValue);
            } else {
                dcm.setNull(tag, vr);
            }
        });

        final String tagValueOut = dcm.getString(tag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, tagValueIn, tagValueOut);
    }
}