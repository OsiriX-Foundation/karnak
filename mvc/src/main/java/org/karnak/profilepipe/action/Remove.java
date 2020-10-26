package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.profilepipe.utils.HMAC;

import java.util.Iterator;
import java.util.Optional;

public class Remove extends AbstractAction {

    public Remove(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        iterator.remove();
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_IN, TagUtils.toString(tag), tag, symbol, dcm.getString(tag).orElse(null));
    }
}
