/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.expression;

import java.util.Objects;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.karnak.backend.util.DicomObjectTools;

public class ExprConditionDestination implements ExpressionItem {

  private int tag;
  private VR vr;
  private String stringValue;
  private final Attributes dcm;
  private final Attributes dcmCopy;

  public ExprConditionDestination(int tag, VR vr) {
    this(tag, vr, new Attributes(), new Attributes());
  }

  public ExprConditionDestination(int tag, VR vr, Attributes dcm, Attributes dcmCopy) {
    this.tag = Objects.requireNonNull(tag);
    this.vr = Objects.requireNonNull(vr);
    if (dcmCopy != null) {
      this.stringValue = dcmCopy.getString(this.tag);
    } else {
      this.stringValue = null;
    }
    this.dcmCopy = dcmCopy;
    this.dcm = dcm;
  }

  public String getString(int tag) {
    return dcmCopy.getString(tag);
  }

  public boolean tagIsPresent(int tag) {
    return DicomObjectTools.containsTagInAllAttributes(tag, dcmCopy);
  }

  public int getTag() {
    return tag;
  }

  public void setTag(int tag) {
    this.tag = tag;
  }

  public VR getVr() {
    return vr;
  }

  public void setVr(VR vr) {
    this.vr = vr;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }
}
