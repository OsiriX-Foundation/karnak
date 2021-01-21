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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "Project")
@Table(name = "project")
public class ProjectEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private byte[] secret;

  @OneToMany(mappedBy = "projectEntity")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<DestinationEntity> destinationEntities;

  @ManyToOne
  @JoinColumn(name = "profile_pipe_id")
  private ProfileEntity profileEntity;

  public ProjectEntity() {
    this.destinationEntities = new ArrayList<>();
  }

  public ProjectEntity(String name, byte[] secret) {
    this.name = name;
    this.secret = secret;
    this.destinationEntities = new ArrayList<>();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getSecret() {
    return secret;
  }

  public void setSecret(byte[] secret) {
    this.secret = secret;
  }

  public List<DestinationEntity> getDestinationEntities() {
    return destinationEntities;
  }

  public void setDestinationEntities(List<DestinationEntity> destinationEntities) {
    this.destinationEntities = destinationEntities;
  }

  public ProfileEntity getProfileEntity() {
    return profileEntity;
  }

  public void setProfileEntity(ProfileEntity profileEntity) {
    this.profileEntity = profileEntity;
  }

  public boolean isNewData() {
    return id == null;
  }
}
