package org.karnak.backend.model;

import org.dcm4che3.data.Attributes;
import org.karnak.backend.dicom.ForwardUtil.Params;

public class Quarantine {
  private Attributes quanrantineAttribute;
  private Params p;

  public Quarantine(Attributes quanrantineAttribute, Params p) {
    this.quanrantineAttribute = quanrantineAttribute;
    this.p = p;
  }

  public Attributes getQuanrantineAttribute() {
    return quanrantineAttribute;
  }

  public void setQuanrantineAttribute(Attributes quanrantineAttribute) {
    this.quanrantineAttribute = quanrantineAttribute;
  }

  public Params getP() {
    return p;
  }

  public void setP(Params p) {
    this.p = p;
  }
}
