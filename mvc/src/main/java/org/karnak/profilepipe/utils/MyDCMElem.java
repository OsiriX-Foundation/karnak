package org.karnak.profilepipe.utils;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;

import java.util.Objects;

public class MyDCMElem {

    private int tag;
    private VR vr;
    private String stringValue;

    public MyDCMElem(int tag, VR vr, DicomObject dcm){

        this.tag = Objects.requireNonNull(tag);
        this.vr = Objects.requireNonNull(vr);
        this.stringValue = dcm.getString(this.tag).orElse(null);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public VR getVr() {
        return vr;
    }

    public void setVr(VR vr) {
        this.vr = vr;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
