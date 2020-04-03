package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;

public class ZReplace implements Action {

    public void execute(Attributes attributes, int tag) {
        VR vr = attributes.getVR(tag);
        attributes.setNull(tag, vr);
    }
}
