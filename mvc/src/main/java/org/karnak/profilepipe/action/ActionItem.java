package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

public interface ActionItem {

    String getSymbol();

    String getDummyValue();

    void setDummyValue(String dummyValue);

    void execute(DicomObject dcm, int tag, String pseudo, String dummy);
}
