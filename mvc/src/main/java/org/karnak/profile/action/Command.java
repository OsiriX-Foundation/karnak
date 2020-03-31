package org.karnak.profile.action;

import org.dcm4che3.data.VR;

public interface Command {
    void execute(VR vr);
}
 