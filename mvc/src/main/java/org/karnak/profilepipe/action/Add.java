package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.profilepipe.utils.HMAC;

import java.util.Iterator;

public class Add extends AbstractAction{

    public Add(String symbol, int newTag, VR vr, String dummyValue) {
        super(symbol, newTag, vr, dummyValue);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        final String tagValueIn = dcm.getString(newTag).orElse(null);

        dcm.get(newTag).ifPresentOrElse(dcmEl -> {
            if (dummyValue != null) {
                dcm.setString(newTag, dcmEl.vr(), dummyValue);
            } else {
                dcm.setNull(newTag, dcmEl.vr());
            }
        }, () -> {
            if (dummyValue != null) {
                dcm.setString(newTag, vr, dummyValue);
            } else {
                dcm.setNull(newTag, vr);
            }
        });

        final String tagValueOut = dcm.getString(newTag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(newTag), newTag, symbol, tagValueIn, tagValueOut);
    }
}