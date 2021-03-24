package org.karnak.backend.enums;

public enum ExternalIDType {
  EXTID_CACHE("External pseudonym is already store in KARNAK"),
  EXTID_IN_TAG("Pseudonym is in a DICOM tag"),
  EXTID_IMPLEMENTATION("");

  private final String value;

  ExternalIDType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
