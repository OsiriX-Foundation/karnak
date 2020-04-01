package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;

public class Replace implements Action{
    public void execute() {
        System.out.println("replace VR LO");
    }

    public void execute(Attributes attributes, int tag) {
        attributes.remove(tag);
    }
}