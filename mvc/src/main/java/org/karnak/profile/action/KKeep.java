package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import java.util.Iterator;

import org.dcm4che6.data.DicomElement;
public class KKeep extends Action{
    private String strAction = "K";

    public String getStrAction() {
        return strAction;
    }

    public void execute(DicomObject attributes, int tag, Iterator<DicomElement> iterator) {}
}