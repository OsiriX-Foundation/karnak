/*
 * Copyright (c) 2009-2019 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.dicom;

import java.util.HashSet;
import java.util.Set;
import org.karnak.backend.model.Quarantine;
import org.weasis.dicom.param.DicomNode;

public class ForwardDicomNode extends DicomNode {

  private final String forwardAETitle;
  private final Set<DicomNode> acceptedSourceNodes;
  private final Long id;
  private Quarantine quarantine;

  public ForwardDicomNode(DicomNode dicomNode) {
    this(dicomNode.getAet(), dicomNode.getHostname());
  }

  public ForwardDicomNode(String fwdAeTitle) {
    this(fwdAeTitle, null);
  }

  public ForwardDicomNode(String fwdAeTitle, String fwdHostname) {
    this(fwdAeTitle, fwdHostname, null);
  }

  public ForwardDicomNode(String fwdAeTitle, String fwdHostname, Long id) {
    super(fwdAeTitle, fwdHostname, null);
    this.forwardAETitle = fwdAeTitle;
    this.id = id;
    this.acceptedSourceNodes = new HashSet<>();
    this.quarantine = null;
  }

  public void addAcceptedSourceNode(String srcAeTitle) {
    addAcceptedSourceNode(srcAeTitle, null);
  }

  public void addAcceptedSourceNode(String srcAeTitle, String srcHostname) {
    acceptedSourceNodes.add(
        new DicomNode(null, srcAeTitle, srcHostname, null, srcHostname != null));
  }

  public void addAcceptedSourceNode(Long id, String srcAeTitle, String srcHostname) {
    acceptedSourceNodes.add(new DicomNode(id, srcAeTitle, srcHostname, null, srcHostname != null));
  }

  public Set<DicomNode> getAcceptedSourceNodes() {
    return acceptedSourceNodes;
  }

  public Quarantine getQuarantine() {
    return quarantine;
  }

  public void setQuarantine(Quarantine quarantine) {
    this.quarantine = quarantine;
  }

  public String getForwardAETitle() {
    return forwardAETitle;
  }

  public Long getId() {
    return id;
  }

  @Override
  public String toString() {
    return forwardAETitle;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + forwardAETitle.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) return false;
    ForwardDicomNode other = (ForwardDicomNode) obj;
    return forwardAETitle.equals(other.forwardAETitle);
  }
}
