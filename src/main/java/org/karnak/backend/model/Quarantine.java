/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
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
