package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;

import java.util.Iterator;
import org.weasis.dicom.param.AttributeEditorContext;

public class ReplaceNull extends AbstractAction {


    public ReplaceNull(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID,
        AttributeEditorContext context) {
        final String tagValueIn = dcm.getString(tag).orElse(null);

        dcm.get(tag).ifPresent(dcmEl -> {
            dcm.setNull(tag, dcmEl.vr());
        });

        final String tagValueOut = dcm.getString(tag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, tagValueIn, tagValueOut);
    }
}
