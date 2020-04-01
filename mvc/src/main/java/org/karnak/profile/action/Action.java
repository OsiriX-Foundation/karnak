package org.karnak.profile.action;

import org.dcm4che3.data.Attributes;

public interface Action {
    void execute(Attributes attributes, int tag);
}
