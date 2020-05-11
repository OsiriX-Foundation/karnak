package org.karnak.profile.option.dummyvalue;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;
import org.karnak.data.AppConfig;
import org.karnak.profile.HMAC;
import org.karnak.profile.option.datemanager.ShiftDate;


public class DefaultDummyValue {

    private ShiftDate shiftDate = new ShiftDate();
    private final int maxDays = 365;
    private final int maxSeconds = 24*60*60;
    private final int nbBytesFormatDA = 8;

    private HMAC hmac;
    {
        hmac = AppConfig.getInstance().getHmac();
    }

    public DefaultDummyValue() {
    }

    public String execute(VR vr, DicomObject dcm, int tag, String patientID) {
        String stringValue = dcm.getString(tag).orElse(null);
        if (stringValue != null) {
            /*
            * SV, UV -> Not present in class dcm4che.VR
            * */
            String dummyValue = switch (vr) {
                case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> unknownValue();
                case DS, FL, FD, IS, SL, SS, UL, US -> zeroValue();
                case AS -> AS(stringValue, patientID);
                case DA -> DA(stringValue, patientID);
                case DT -> DT(stringValue, patientID);
                case TM -> TM(stringValue, patientID);
                case UI -> UI();
                default -> notImplemented();
            };
            System.out.println(stringValue);
            System.out.println(dummyValue);
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

    private String AS(String age, String patientID) {
        double shiftDays = this.hmac.scaleHash(patientID, 0, this.maxDays);
        return this.shiftDate.ASshiftByDays(age, (int) shiftDays);
    }

    private String TM(String time, String patientID) {
        double shiftSeconds = this.hmac.scaleHash(patientID, 0, this.maxSeconds);
        return this.shiftDate.TMshiftBySeconds(time, (int) shiftSeconds);
    }

    private String DA(String date, String patientID) {
        double shiftDays = this.hmac.scaleHash(patientID, 0, this.maxDays);
        return this.shiftDate.DAshiftByDays(date, (int) shiftDays);
    }

    private String DT(String datetime, String patientID) {
        String dummyDate = null;
        int datetimeLength = datetime.length();
        if (datetimeLength > this.nbBytesFormatDA) {
            String date = datetime.substring(0, this.nbBytesFormatDA);
            dummyDate = DA(date, patientID);
            String dummyTime = null;
            String time = datetime.substring(8, datetimeLength);
            dummyTime = TM(time, patientID);
            return dummyDate.concat(dummyTime);
        }
        dummyDate = DA(datetime, patientID);
        return dummyDate;
    }

    private String UI() {
        return UIDUtils.randomUID();
    }
}
