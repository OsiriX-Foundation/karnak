package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomObject;

@FunctionalInterface
public interface ActionStrategy {
    enum Output {TO_REMOVE, APPLIED, NEXT_PROFILE, PRESERVED}

    Output execute(DicomObject dcm, int tag, String pseudo, String dummy);
}