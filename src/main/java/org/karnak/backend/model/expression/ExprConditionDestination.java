package org.karnak.backend.model.expression;

import java.util.Objects;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.backend.util.DicomObjectTools;

public class ExprConditionDestination implements ExpressionItem {

  private int tag;
  private VR vr;
  private String stringValue;
  private final DicomObject dcm;
  private final DicomObject dcmCopy;

  public ExprConditionDestination(int tag, VR vr, DicomObject dcm, DicomObject dcmCopy) {
    this.tag = Objects.requireNonNull(tag);
    this.vr = Objects.requireNonNull(vr);
    if (dcmCopy != null) {
      this.stringValue = dcmCopy.getString(this.tag).orElse(null);
    } else {
      this.stringValue = null;
    }
    this.dcmCopy = dcmCopy;
        this.dcm = dcm;
    }

    public String getString(int tag){
        return dcmCopy.getString(tag).orElse(null);
    }

    public boolean tagIsPresent(int tag){
        return DicomObjectTools.tagIsInDicomObject(tag, dcmCopy);
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
