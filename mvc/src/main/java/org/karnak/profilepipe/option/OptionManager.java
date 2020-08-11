package org.karnak.profilepipe.option;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.karnak.data.AppConfig;
import org.karnak.profilepipe.action.Action;
import org.karnak.profilepipe.action.ActionStrategy;

public class OptionManager {

    public static String getActionReplace(DicomObject dcm, DicomElement dcmEl, String patientID, String option, String args){
        if(option == null){
            return null;
        }
        return switch (option) {
            case "shiftDate" -> "19930822"; //call the function that executes the algo for shift date and return this value
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
