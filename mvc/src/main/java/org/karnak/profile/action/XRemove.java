package org.karnak.profile.action;

import java.util.Iterator;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

public class XRemove extends Action {
    private String strAction = "X";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator) {
        iterator.remove();
    }
}