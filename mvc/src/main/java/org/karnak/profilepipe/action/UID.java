package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;

import java.util.Iterator;

public class UID extends AbstractAction {

    public UID(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String patientID) {
        final String tagValueIn = dcm.getString(tag).orElse(null);

        String uidValue = dcm.getString(tag).orElse(null);
        String uidHashed = AppConfig.getInstance().getHmac().uidHash(patientID, uidValue);
        dcm.setString(tag, VR.UI, uidHashed);

        final String tagValueOut = dcm.getString(tag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, tagValueIn, tagValueOut);
    }
}
