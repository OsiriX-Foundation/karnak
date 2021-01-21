/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
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
import javax.persistence.Table;

@Entity(name = "SOPClassUID")
@Table(name = "sop_class_uid")
public class SOPClassUIDEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String ciod;
  private String uid;
  private String name;

  public SOPClassUIDEntity() {}

  public SOPClassUIDEntity(String ciod, String uid, String name) {
    this.ciod = ciod;
    this.uid = uid;
    this.name = name;
  }

  public SOPClassUIDEntity(String ciod) {
    this.ciod = ciod;
  }

  public String getCiod() {
    return ciod;
  }

  public void setCiod(String ciod) {
    this.ciod = ciod;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
