package org.karnak.profilepipe.option;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.profilepipe.option.datemanager.ShiftDate;

public class OptionManager {

    public static String getActionReplace(DicomObject dcm, DicomElement dcmEl, String patientID, String option, String args){
        ShiftDate shiftDate = new ShiftDate();
        if(option == null){
            return null;
        }
        return switch (option) {
            case "shiftDate" -> shiftDate.days(dcm, dcmEl, patientID, args); //call the function that executes the algo for shift date and return this value
            case "dummyValue" ->  replaceByDummy(args);
            default -> null;
        };
    }

    public static String replaceByDummy(String args){
        if (args == null){
            return "";
        } else {
            return args;
        }
    }
}
