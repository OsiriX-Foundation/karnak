package org.karnak.profile.option.dummyvalue;

import java.util.Random;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;
import org.karnak.profile.HMAC;
import org.karnak.profile.option.datemanager.RandomUtils;
import org.karnak.profile.option.datemanager.ShiftDate;


public class DefaultDummyValue {

    private Random random;
    private HMAC hmac = new HMAC();
    private final int maxDays = 365;
    private final int maxSeconds = 24*60*60;
    private final int nbBytesFormatDA = 8;

    public DefaultDummyValue() {
    }

    public String execute(VR vr, DicomObject dcm, int tag) {
        String stringValue = dcm.getString(tag).orElse(null);
        String studyInstanceUID = dcm.getString(Tag.StudyInstanceUID).orElse(null);
        if (stringValue != null) {
            long seed = this.hmac.longHash(stringValue);
            this.random = new Random(seed);
            /*
            * AT -> ?
            * OB, OD, ... -> ?
            * SV, UV -> Not present in class dcm4che.VR
            * */
            String dummyValue = switch (vr) {
                case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> unknownValue();
                case DS, FL, FD, IS, SL, SS, UL, US -> zeroValue();
                case AS -> AS();
                case DA -> DA(stringValue, studyInstanceUID);
                case DT -> DT(stringValue, studyInstanceUID);
                case TM -> TM(stringValue, studyInstanceUID);
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
        return shiftDate.TMshiftByRandomSeconds(time, this.maxSeconds);
    }

    private String DA(String date, String StudyInstanceUID) {
        ShiftDate shiftDate = new ShiftDate(StudyInstanceUID);
        return shiftDate.DAshiftByRandomDays(date, this.maxDays);
    }

    private String DT(String datetime, String StudyInstanceUID) {
        String dummyDate = null;
        int datetimeLength = datetime.length();
        if (datetimeLength > this.nbBytesFormatDA) {
            String date = datetime.substring(0, this.nbBytesFormatDA);
            dummyDate = DA(date, StudyInstanceUID);
            String dummyTime = null;
            String time = datetime.substring(8, datetimeLength);
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
