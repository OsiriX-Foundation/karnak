package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;

import java.util.Iterator;

public interface ActionItem {

    String getSymbol();

    String getDummyValue();

    void setDummyValue(String dummyValue);

    void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, String pseudo);
}
