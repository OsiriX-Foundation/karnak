package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.data.AppConfig;

import java.util.Optional;

public enum Action implements ActionStrategy {

    REPLACE("D", (dcm, tag, pseudo, dummy) -> {
        dcm.get(tag).ifPresent(dcmEl -> {
            if (dummy != null) {
                dcm.setString(tag, dcmEl.vr(), dummy);
            } else {
                dcm.setNull(tag, dcmEl.vr());
            }
        });
        return Output.APPLIED;
    }),

    DEFAULT_DUMMY("DDum", (dcm, tag, pseudo, dummy) -> {
        final String tagValue = dcm.getString(tag).orElse(null);
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
            case UI -> AppConfig.getInstance().getHmac().uidHash(pseudo, tagValue);
            default -> null;
        };
        return REPLACE.execute(dcm, tag, pseudo, defaultDummyValue);
    }),

    KEEP("K", (dcm, tag, pseudo, dummy) -> Output.PRESERVED),

    REMOVE("X", (dcm, tag, pseudo, dummy) -> Output.TO_REMOVE),

    REPLACE_NULL("Z", (dcm, tag, pseudo, dummy) -> {
        dcm.get(tag).ifPresent(dcmEl -> {
            dcm.setNull(tag, dcmEl.vr());
        });
        return Output.APPLIED;
    }),

    UID("U", (dcm, tag, pseudo, dummy) -> {
        String uidValue = dcm.getString(tag).orElse(null);
        String uidHashed = AppConfig.getInstance().getHmac().uidHash(pseudo, uidValue);
        System.out.println(uidValue + " - " + uidHashed);
        dcm.setString(tag, VR.UI, uidHashed);
        return Output.APPLIED;
    });

    private final String symbol;
    private final ActionStrategy action;

    Action(String symbol, ActionStrategy action) {
        this.symbol = symbol;
        this.action = action;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public Output execute(DicomObject dcm, int tag, String pseudo, String dummy) {
        return action.execute(dcm, tag, pseudo, dummy);
    }

    public static Action convertAction(String action) {
        if (action == null) {
            return null;
        }
        return switch (action) {
            case "Z" -> Action.REPLACE_NULL;
            case "X" -> Action.REMOVE;
            case "K" -> Action.KEEP;
            case "U" -> Action.UID;
            case "DDum" -> Action.DEFAULT_DUMMY;
            case "D" -> Action.REPLACE;
            default -> null;
        };
    }
}
