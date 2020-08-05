package org.karnak.profilepipe.utils;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;

public class MyDCMElem {

    private int tag;
    private VR vr;
    private String stringValue;
    private Float floatValue;
    private Double doubleValue;
    private Integer integerValue;

    public MyDCMElem(int tag, VR vr, DicomObject dcm){
        this.tag = tag;
        this.vr = vr;
        this.stringValue = dcm.getString(this.tag).orElse(null);
        /*this.floatValue = dcm.getFloat(this.tag).or(0);
        this.doubleValue = dcm.getDouble(this.tag).orElse(0);
        this.integerValue = dcm.getInt(this.tag).orElse(0);*/
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

    /*public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }*/
}
