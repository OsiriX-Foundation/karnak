package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;

public class Replace implements Action{

    private Algorithm algo = new Algorithm();

    public void execute(Attributes attributes, int tag) {   
        VR vr = attributes.getVR(tag);
        String vrValue = this.algo.execute(vr);
        attributes.setValue(tag, vr, vrValue);
    }
}