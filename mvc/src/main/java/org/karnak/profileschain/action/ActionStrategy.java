package org.karnak.profileschain.action;

import org.dcm4che6.data.DicomObject;
import org.karnak.profileschain.ProfileChain;

@FunctionalInterface
public interface ActionStrategy {
    enum Output {TO_REMOVE, APPLIED, NEXT_PROFILE, PRESERVED}

    Output execute(DicomObject dcm, int tag, String pseudo, String dummy);
}