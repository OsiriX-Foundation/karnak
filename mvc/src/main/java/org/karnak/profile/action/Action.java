package org.karnak.profile.action;

import org.dcm4che6.data.DicomObject;

@FunctionalInterface
public interface Action {
    void execute(DicomObject attributes, int tag);
}
