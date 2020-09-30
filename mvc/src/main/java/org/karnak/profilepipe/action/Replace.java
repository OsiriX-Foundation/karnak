package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;

import java.util.Iterator;
import org.weasis.dicom.param.AttributeEditorContext;

public class Replace extends AbstractAction {

    public Replace(String symbol) {
        super(symbol);
    }

    public Replace(String symbol, String dummyValue) {
        super(symbol, dummyValue);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID,
        AttributeEditorContext context) {
        final String tagValueIn = dcm.getString(tag).orElse(null);

        dcm.get(tag).ifPresent(dcmEl -> {
            if (dummyValue != null) {
                dcm.setString(tag, dcmEl.vr(), dummyValue);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });

        final String tagValueOut = dcm.getString(tag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, tagValueIn, tagValueOut);
    }
}
