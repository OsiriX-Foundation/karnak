/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "secret")
public class SecretEntity implements Serializable {

  private Long id;
  private Long projectId;
  private ProjectEntity projectEntity;
  private byte[] key;
  private LocalDateTime creationDate;
  private boolean active;

  public SecretEntity() {}

  public SecretEntity(byte[] key) {
    this.key = key;
    this.creationDate = LocalDateTime.now();
  }

  public SecretEntity(Long projectId, byte[] key) {
    this.projectId = projectId;
    this.key = key;
    this.creationDate = LocalDateTime.now();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "project_id")
  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  @ManyToOne
  @JoinColumn(name = "project_id", insertable = false, updatable = false)
  public ProjectEntity getProjectEntity() {
    return projectEntity;
  }

  public void setProjectEntity(ProjectEntity projectEntity) {
    this.projectEntity = projectEntity;
  }

  public byte[] getKey() {
    return key;
  }

  public void setKey(byte[] key) {
    this.key = key;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
