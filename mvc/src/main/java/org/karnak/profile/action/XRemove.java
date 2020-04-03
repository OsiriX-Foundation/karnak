package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;

public class XRemove implements Action{
    public void execute(Attributes attributes, int tag) {
        attributes.remove(tag);
    }
}