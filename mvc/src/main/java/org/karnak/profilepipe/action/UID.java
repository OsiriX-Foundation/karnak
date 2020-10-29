package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;
import org.karnak.profilepipe.utils.HMAC;

import java.util.Iterator;

public class UID extends AbstractAction {

    public UID(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        String uidValue = dcm.getString(tag).orElse(null);
        String uidHashed = null;
        if (uidValue != null) {
            uidHashed = hmac.uidHash(uidValue);
            dcm.setString(tag, VR.UI, uidHashed);
        }
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, uidValue, uidHashed);
    }
}
