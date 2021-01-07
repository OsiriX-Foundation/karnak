package org.karnak.backend.model.action;

import java.util.Iterator;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.model.profilepipe.HMAC;
import org.slf4j.MDC;

public class Replace extends AbstractAction {

    public Replace(String symbol) {
        super(symbol);
    }

    public Replace(String symbol, String dummyValue) {
        super(symbol, dummyValue);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        String tagValueIn = dcm.getString(tag).orElse(null);

        dcm.get(tag).ifPresent(dcmEl -> {
            if (dummyValue != null) {
                dcm.setString(tag, dcmEl.vr(), dummyValue);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });
        LOGGER.trace(CLINICAL_MARKER, PATTERN_WITH_INOUT, MDC.get("SOPInstanceUID"), TagUtils.toString(tag), symbol, tagValueIn, dummyValue);
    }
}
