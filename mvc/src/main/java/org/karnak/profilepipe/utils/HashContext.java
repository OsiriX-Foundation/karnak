package org.karnak.profilepipe.utils;

public class HashContext {
    private byte[] secret;
    private String PatientID;

    public HashContext(byte[] secret, String PatientID) {
        this.secret = secret;
        this.PatientID = PatientID;
    }

    public byte[] getSecret() {
        return secret;
    }

    public String getPatientID() {
        return PatientID;
    }
}
