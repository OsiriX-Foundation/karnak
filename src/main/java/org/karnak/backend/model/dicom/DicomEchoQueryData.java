package org.karnak.backend.model.dicom;

public class DicomEchoQueryData {

  private static final String DEFAULT_VALUE_FOR_CALLING_AET = "DCM-TOOLS";

  private String callingAet;
  private DicomNodeList calledDicomNodeType;
  private String calledAet;
  private String calledHostname;
  private Integer calledPort;

  public DicomEchoQueryData() {
    reset();
  }

  public String getCallingAet() {
    return callingAet;
  }

  public void setCallingAet(String callingAeTitle) {
    this.callingAet = callingAeTitle;
  }

  public DicomNodeList getCalledDicomNodeType() {
    return calledDicomNodeType;
  }

  public void setCalledDicomNodeType(DicomNodeList calledDicomNodeType) {
    this.calledDicomNodeType = calledDicomNodeType;
  }

  public String getCalledAet() {
    return calledAet;
  }

  public void setCalledAet(String calledAet) {
    this.calledAet = calledAet;
  }

  public String getCalledHostname() {
    return calledHostname;
  }

  public void setCalledHostname(String calledHostname) {
    this.calledHostname = calledHostname;
  }

  public Integer getCalledPort() {
    return calledPort;
  }

  public void setCalledPort(Integer calledPort) {
    this.calledPort = calledPort;
  }

  public void reset() {
    callingAet = DEFAULT_VALUE_FOR_CALLING_AET;
  }
}
