package org.karnak.expression;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.karnak.profilepipe.utils.DicomObjectTools;

import java.util.Objects;

public class ExprConditionDestination implements ExpressionItem{

    private int tag;
    private VR vr;
    private String stringValue;
    private DicomObject dcm;
    private DicomObject dcmCopy;

    public ExprConditionDestination(int tag, VR vr, DicomObject dcm, DicomObject dcmCopy){
        this.tag = Objects.requireNonNull(tag);
        this.vr = Objects.requireNonNull(vr);
        this.stringValue = dcmCopy.getString(this.tag).orElse(null);
        this.dcmCopy = dcmCopy;
        this.dcm = dcm;
    }

    public String getString(int tag){
        return dcmCopy.getString(tag).orElse(null);
    }

    public boolean tagIsPresent(int tag){
        return DicomObjectTools.tagIsInDicomObject(tag, dcmCopy);
    }

}
