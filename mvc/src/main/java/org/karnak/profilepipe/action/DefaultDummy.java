package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.profilepipe.utils.HMAC;

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
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        Optional<DicomElement> dcmItem = dcm.get(tag);
        DicomElement dcmEl = dcmItem.get();
        VR vr = dcmEl.vr();
        String defaultDummyValue = switch (vr) {
            case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> "UNKNOWN";
            case DS, IS -> "0";
            case AS -> "045Y";
            case DA -> "19991111";
            case DT -> "19991111111111";
            case TM -> "111111";
            case UI -> hmac.uidHash(dcm.getString(tag).orElse(null));
            default -> null;
        };
        ActionItem replace = new Replace(symbol, defaultDummyValue);
        replace.execute(dcm, tag, iterator, hmac);
    }
}
