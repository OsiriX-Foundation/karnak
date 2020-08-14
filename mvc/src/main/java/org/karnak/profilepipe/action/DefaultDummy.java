package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.AppConfig;

import java.util.Iterator;
import java.util.Optional;

public class DefaultDummy extends AbstractAction {

    public DefaultDummy(String symbol) {
        super(symbol);
    }

    public DefaultDummy(String symbol, String dummyValue) {
        super(symbol, dummyValue);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudo) {
        final String tagValueIn = dcm.getString(tag).orElse(null);

        final Optional<DicomElement> dcmItem = dcm.get(tag);
        final DicomElement dcmEl = dcmItem.get();
        final VR vr = dcmEl.vr();
        String defaultDummyValue = switch (vr) {
            case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> "UNKNOWN";
            case DS, FL, FD, IS, SL, SS, UL, US -> "0";
            case AS -> "045Y";
            case DA -> "19991111";
            case DT -> "19991111111111";
            case TM -> "111111";
            case UI -> AppConfig.getInstance().getHmac().uidHash(pseudo, tagValueIn);
            default -> null;
        };
        final ActionItem replace = new Replace(symbol, defaultDummyValue);
        replace.execute(dcm, tag, iterator, pseudo);
        final String tagValueOut = dcm.getString(tag).orElse(null);
        LOGGER.info(CLINICAL_MARKER, PATTERN_WITH_INOUT, TagUtils.toString(tag), tag, symbol, tagValueIn, tagValueOut);
    }
}
