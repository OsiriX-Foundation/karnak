package org.karnak.backend.dicom;

import java.io.InputStream;
import org.dcm4che3.net.Association;

public final class Params {

  private final String iuid;
  private final String cuid;
  private final String tsuid;
  private final InputStream data;
  private final Association as;
  private final int priority;

  public Params(
      String iuid, String cuid, String tsuid, int priority, InputStream data, Association as) {
    super();
    this.iuid = iuid;
    this.cuid = cuid;
    this.tsuid = tsuid;
    this.priority = priority;
    this.as = as;
    this.data = data;
  }

  public String getIuid() {
    return iuid;
  }

  public String getCuid() {
    return cuid;
  }

  public String getTsuid() {
    return tsuid;
  }

  public int getPriority() {
    return priority;
  }

  public Association getAs() {
    return as;
  }

  public InputStream getData() {
    return data;
  }
}
