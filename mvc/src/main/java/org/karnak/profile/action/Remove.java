package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;

public class Remove implements Action{
    public void execute() {
        System.out.println("remove VR LO");
    }

    public void execute(Attributes attributes, int tag) {
        attributes.remove(tag);
    }
}