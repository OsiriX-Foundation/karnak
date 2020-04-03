package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;

import java.util.Random;

public class UUID implements Action {
    public void execute(Attributes attributes, int tag) {
        attributes.setString(tag, VR.UI, "2.16.840.1.113669.632.20.1211."+new Random().nextInt(536871066));
    }
}
