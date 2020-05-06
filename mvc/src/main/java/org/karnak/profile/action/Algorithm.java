package org.karnak.profile.action;

import java.util.Random;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;


public class Algorithm {

    private Random random;
    private HMAC hmac = new HMAC();

    public Algorithm() {
    }

    public String execute(VR vr, String stringValue, String StudyInstanceUID) {
        if (stringValue != null) {
            long seed = this.hmac.longHash(stringValue);
            this.random = new Random(seed);
            String dummyValue = switch (vr) {
                case AE, CS, LO, LT, PN, SH, ST, UN, UT -> unknownValue();
                case DS, FL, FD, IS, SL, SS, UL, US -> zeroValue();
                case AS -> AS();
                case DA -> DA(stringValue, StudyInstanceUID);
                case DT -> DT(stringValue, StudyInstanceUID);
                case TM -> TM(stringValue, StudyInstanceUID);
                case UI -> UI();
                default -> notImplemented();
            };
            return dummyValue;
        }
        return null;
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
        // shift Date
        return RandomUtils.randomAS(this.random);
    }

    private String TM(String time, String StudyInstanceUID) {
        ShiftDate shiftDate = new ShiftDate(StudyInstanceUID);
        String[] timeSplit = time.split("\\.");
        return shiftDate.TMshiftByRandomSeconds(timeSplit[0], 24*360);
    }

    private String DA(String date, String StudyInstanceUID) {
        ShiftDate shiftDate = new ShiftDate(StudyInstanceUID);
        return shiftDate.DAshiftByRandomDays(date, 120);
    }

    private String DT(String datetime, String StudyInstanceUID) {
        String date = datetime.substring(0, 8);
        String dummyDate = DA(date, StudyInstanceUID);
        String dummyTime = null;
        if (datetime.length() > 8) {
            String time = datetime.substring(8, 14);
            dummyTime = TM(time, StudyInstanceUID);
            return dummyDate.concat(dummyTime);
        }
        return dummyDate;
    }

    private String UI() {
        return UIDUtils.randomUID();
    }
}
