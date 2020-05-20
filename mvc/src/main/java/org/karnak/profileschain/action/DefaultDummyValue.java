package org.karnak.profileschain.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.AppConfig;
import org.karnak.profileschain.action.Action;
import org.karnak.profileschain.action.DReplace;
import org.karnak.profileschain.utils.HMAC;

import java.util.Iterator;
import java.util.Optional;

public class DefaultDummyValue implements Action {
    public DefaultDummyValue() {
    }
    private HMAC hmac;{
        hmac = AppConfig.getInstance().getHmac();
    }
    public String getStrAction() {
        return "defaultDummyValue";
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudonym, String dummyValue) {
        final String tagValue = dcm.getString(tag).orElse(null);
        final Optional<DicomElement> dcmItem = dcm.get(tag);
        final DicomElement dcmEl = dcmItem.get();
        final VR vr = dcmEl.vr();
        String defaultDummyValue = switch (vr) {
            case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> unknownValue();
            case DS, FL, FD, IS, SL, SS, UL, US -> zeroValue();
            case AS -> AS();
            case DA -> DA();
            case DT -> DT();
            case TM -> TM();
            case UI -> UI(pseudonym, tagValue);
            default -> notImplemented();
        };
        final Action dReplace = new DReplace();
        dReplace.execute(dcm, tag, iterator, pseudonym, defaultDummyValue);
    }

    private String notImplemented() {
        return null;
    }

    private String unknownValue() {
        return "UNKNOWN";
    }

    private String zeroValue() {
        return "0";
    }

    private String AS() {
        return "045Y";
    }

    private String TM() {
        return "111111";
    }

    private String DA() {
        return "19991111";
    }

    private String DT() {
        return "19991111111111";
    }

    private String UI(String pseudonym, String uidValue) {
        return hmac.uidHash(pseudonym, uidValue);
    }
}
