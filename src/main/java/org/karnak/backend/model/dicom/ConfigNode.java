package org.karnak.backend.model.dicom;

import java.util.Objects;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.DicomNode;

public class ConfigNode {

  private String name;
  private DicomNode calledNode;

  public ConfigNode(String name, DicomNode calledNode) {
    this.name = Objects.requireNonNull(name);
    this.calledNode = Objects.requireNonNull(calledNode);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (StringUtil.hasText(name)) {
      this.name = name;
    }
  }

  public String getAet() {
    return calledNode.getAet();
  }

  public String getHostname() {
    return calledNode.getHostname();
  }

  public Integer getPort() {
    return calledNode.getPort();
  }

  public DicomNode getCalledNode() {
    return calledNode;
  }

  public void setCalledNode(DicomNode calledNode) {
    if (calledNode != null) {
      this.calledNode = calledNode;
    }
  }
}
