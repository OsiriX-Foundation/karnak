package org.karnak.backend.model.profilepipe;

public class HashContext {

  private final byte[] secret;
  private final String PatientID;

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
