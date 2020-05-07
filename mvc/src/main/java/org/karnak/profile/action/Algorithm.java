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
            /*
            WONDERFULL TEST
             */
            System.out.println(DT("2012", StudyInstanceUID));
            System.out.println(DT("201212", StudyInstanceUID));
            System.out.println(DT("20121231", StudyInstanceUID));
            System.out.println(DT("2012123108", StudyInstanceUID));
            System.out.println(DT("201212310845", StudyInstanceUID));
            System.out.println(DT("20121231084559", StudyInstanceUID));
            System.out.println(DT("20121231084559.1", StudyInstanceUID));
            System.out.println(DT("20121231084559.12", StudyInstanceUID));
            System.out.println(DT("20121231084559.123", StudyInstanceUID));
            System.out.println(DT("20121231084559.1234", StudyInstanceUID));
            System.out.println(DT("20121231084559.12345", StudyInstanceUID));
            System.out.println(DT("20121231084559.123456", StudyInstanceUID));
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
        return shiftDate.TMshiftByRandomSeconds(time, 24*360);
    }

    private String DA(String date, String StudyInstanceUID) {
        ShiftDate shiftDate = new ShiftDate(StudyInstanceUID);
        return shiftDate.DAshiftByRandomDays(date, 120);
    }

    private String DT(String datetime, String StudyInstanceUID) {
        String dummyDate = null;
        if (datetime.length() > 8) {
            String date = datetime.substring(0, 8);
            dummyDate = DA(date, StudyInstanceUID);
            String dummyTime = null;
            String time = datetime.substring(8, datetime.length());
            dummyTime = TM(time, StudyInstanceUID);
            return dummyDate.concat(dummyTime);
        }
        dummyDate = DA(datetime, StudyInstanceUID);
        return dummyDate;
    }

    private String UI() {
        return UIDUtils.randomUID();
    }
}
