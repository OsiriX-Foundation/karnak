package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;

import java.nio.ByteBuffer;

public class DReplace implements Action{

    private Algorithm algo = new Algorithm();

    public void execute(Attributes attributes, int tag) {   
        VR vr = attributes.getVR(tag);
        Object value = attributes.getValue(tag);
        String valueString = value.toString();
        System.out.println(valueString);
        int seed = 3; // ByteBuffer.wrap(value).getInt();
        String vrValue = this.algo.execute(vr, seed);
        if (vrValue != "-1") {
            attributes.setValue(tag, vr, vrValue);
        }
    }
}