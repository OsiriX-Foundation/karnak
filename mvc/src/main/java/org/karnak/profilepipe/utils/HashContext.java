package org.karnak.profilepipe.utils;

public class HashContext {
    private String secret;
    private String PatientID;

    public HashContext(String secret, String PatientID) {
        this.secret = secret;
        this.PatientID = PatientID;
    }

    public String getSecret() {
        return secret;
    }

    public String getPatientID() {
        return PatientID;
    }
}
