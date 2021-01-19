/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "DicomSourceNode")
@Table(name = "dicom_source_node")
public class DicomSourceNodeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String description;

  // AETitle of the source node.
  @NotBlank(message = "AETitle is mandatory")
  @Size(max = 16, message = "AETitle has more than 16 characters")
  private String aeTitle;

  // the host or IP of the source node. If the hostname exists then it is checked
  // (allows a restriction on the host not only in the AETitle).
  private String hostname;

  // if "true" check the hostname during the DICOM association and if not match
  // the connection is abort
  private Boolean checkHostname;

  @ManyToOne
  @JoinColumn(name = "forward_node_id")
  private ForwardNodeEntity forwardNodeEntity;

  protected DicomSourceNodeEntity() {
    this.description = "";
    this.aeTitle = "";
    this.hostname = "";
    this.checkHostname = Boolean.FALSE;
  }

  public static DicomSourceNodeEntity ofEmpty() {
    DicomSourceNodeEntity instance = new DicomSourceNodeEntity();
    return instance;
  }

  public Long getId() {
    return id;
  }

  public boolean isNewData() {
    return id == null;
  }

  public String getStringReference() {
    return getAeTitle();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAeTitle() {
    return aeTitle;
  }

  public void setAeTitle(String aeTitle) {
    this.aeTitle = aeTitle;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public ForwardNodeEntity getForwardNodeEntity() {
    return forwardNodeEntity;
  }

  public void setForwardNodeEntity(ForwardNodeEntity forwardNodeEntity) {
    this.forwardNodeEntity = forwardNodeEntity;
  }

  public Boolean getCheckHostname() {
    return checkHostname;
  }

  public void setCheckHostname(Boolean checkHostname) {
    this.checkHostname = checkHostname;
  }

  /**
   * Informs if this object matches with the filter as text.
   *
   * @param filterText the filter as text.
   * @return true if this object matches with the filter as text; false otherwise.
   */
  public boolean matchesFilter(String filterText) {
    return contains(description, filterText) //
        || contains(aeTitle, filterText) //
        || contains(hostname, filterText);
  }

  private boolean contains(String value, String filterText) {
    return value != null && value.contains(filterText);
  }

  @Override
  public String toString() {
    return "DicomSourceNode [id="
        + id
        + ", description="
        + description
        + ", aeTitle="
        + aeTitle
        + ", hostname="
        + hostname
        + ", checkHostname="
        + checkHostname
        + "]";
  }
}
