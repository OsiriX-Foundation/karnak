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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "Profile")
@Table(name = "profile")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
  "name",
  "version",
  "minimumKarnakVersion",
  "defaultIssuerOfPatientID",
  "profileElementEntities",
  "maskEntities"
})
public class ProfileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  private String name;
  private String version;
  private String minimumKarnakVersion;
  private String defaultissueropatientid;

  @JsonIgnore private Boolean bydefault;

  @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<ProfileElementEntity> profileElementEntities = new ArrayList<>();

  @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<MaskEntity> maskEntities = new HashSet<>();

  @OneToMany(mappedBy = "profileEntity")
  @LazyCollection(LazyCollectionOption.FALSE)
  @JsonIgnore
  private List<ProjectEntity> projectEntities;

  public ProfileEntity() {}

  public ProfileEntity(
      String name, String version, String minimumKarnakVersion, String defaultissueropatientid) {
    this.name = name;
    this.version = version;
    this.minimumKarnakVersion = minimumKarnakVersion;
    this.defaultissueropatientid = defaultissueropatientid;
    this.bydefault = false;
  }

  public ProfileEntity(
      String name,
      String version,
      String minimumKarnakVersion,
      String defaultissueropatientid,
      Boolean bydefault) {
    this.name = name;
    this.version = version;
    this.minimumKarnakVersion = minimumKarnakVersion;
    this.defaultissueropatientid = defaultissueropatientid;
    this.bydefault = bydefault;
  }

  public Long getId() {
    return id;
  }

  public void addProfilePipe(ProfileElementEntity profileElementEntity) {
    this.profileElementEntities.add(profileElementEntity);
  }

  public void addMask(MaskEntity maskEntity) {
    this.maskEntities.add(maskEntity);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty("minimumKarnakVersion")
  public String getMinimumkarnakversion() {
    return minimumKarnakVersion;
  }

  public void setMinimumkarnakversion(String minimumKarnakVersion) {
    this.minimumKarnakVersion = minimumKarnakVersion;
  }

  @JsonProperty("defaultIssuerOfPatientID")
  public String getDefaultissueropatientid() {
    return defaultissueropatientid;
  }

  public void setDefaultissueropatientid(String defaultissueropatientid) {
    this.defaultissueropatientid = defaultissueropatientid;
  }

  public List<ProfileElementEntity> getProfileElementEntities() {
    return profileElementEntities;
  }

  public void setProfileElementEntities(List<ProfileElementEntity> profileElementEntities) {
    this.profileElementEntities = profileElementEntities;
  }

  public Boolean getBydefault() {
    return bydefault;
  }

  public void setBydefault(Boolean bydefault) {
    this.bydefault = bydefault;
  }

  public Set<MaskEntity> getMaskEntities() {
    return maskEntities;
  }

  public void setMaskEntities(Set<MaskEntity> maskEntities) {
    this.maskEntities = maskEntities;
  }

  public List<ProjectEntity> getProjectEntities() {
    return projectEntities;
  }
}
