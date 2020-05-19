package org.karnak.profileschain.action;

import java.util.Iterator;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

public class XRemove implements Action {
    private String strAction = "X";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudonym, String dummyValue) {
        iterator.remove();
    }
}