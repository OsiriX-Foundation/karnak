package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.profilepipe.utils.HMAC;

import java.util.Iterator;

public interface ActionItem {

    String getSymbol();

    String getDummyValue();

    void setDummyValue(String dummyValue);

    VR getVr();

    void setVr(VR vr);

    void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac);
}
