package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;

public class UUID implements Action {
    public void execute(DicomObject dcm, int tag) {
        dcm.setString(tag, VR.UI, UIDUtils.randomUID());
    }
}
