package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;

import java.util.Iterator;
import java.util.Optional;
import org.weasis.dicom.param.AttributeEditorContext;

public class Remove extends AbstractAction {

    public Remove(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID,
        AttributeEditorContext context) {
        final String tagValueIn = dcm.getString(tag).orElse(null);
        final Optional<DicomElement> dcmItem = dcm.get(tag);

        iterator.remove();
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(tag), tag, symbol, tagValueIn);
    }
}
