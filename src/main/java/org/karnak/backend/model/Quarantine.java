package org.karnak.backend.model;

import org.dcm4che3.data.Attributes;
import org.karnak.backend.dicom.ForwardUtil.Params;

public class Quarantine {
  private Attributes attributes;
  private Params params;

  public Quarantine(Attributes quanrantineAttribute, Params p) {
    this.attributes = quanrantineAttribute;
    this.params = p;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public void setAttributes(Attributes attributes) {
    this.attributes = attributes;
  }

  public Params getParams() {
    return params;
  }

  public void setParams(Params params) {
    this.params = params;
  }
}
