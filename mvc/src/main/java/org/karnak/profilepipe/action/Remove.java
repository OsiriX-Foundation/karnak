package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.profilepipe.utils.HMAC;
import org.slf4j.MDC;

import java.util.Iterator;
import java.util.Optional;

public class Remove extends AbstractAction {

    public Remove(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        String tagValueIn = dcm.getString(tag).orElse(null);
        iterator.remove();
        LOGGER.trace(CLINICAL_MARKER, PATTERN_WITH_IN, MDC.get("SOPInstanceUID"), TagUtils.toString(tag), symbol, tagValueIn);
    }
}
