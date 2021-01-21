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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.karnak.backend.data.converter.ArgumentToMapConverter;
import org.karnak.backend.data.converter.TagListToStringListConverter;

@Entity(name = "ProfileElement")
@Table(name = "profile_element")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileElementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  private String name;
  private String codename;
  private String condition;
  private String action;
  private String option;
  @JsonIgnore private Integer position;

  @ManyToOne()
  @JoinColumn(name = "profile_id", nullable = false)
  @JsonIgnore
  private ProfileEntity profileEntity;

  @OneToMany(mappedBy = "profileElementEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonSerialize(converter = TagListToStringListConverter.class)
  private List<IncludedTagEntity> includedTagEntities = new ArrayList<>();

  @OneToMany(mappedBy = "profileElementEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonSerialize(converter = TagListToStringListConverter.class)
  private List<ExcludedTagEntity> excludedTagEntities = new ArrayList<>();

  @OneToMany(mappedBy = "profileElementEntity", cascade = CascadeType.ALL)
  @LazyCollection(LazyCollectionOption.FALSE)
  @JsonSerialize(converter = ArgumentToMapConverter.class)
  private List<ArgumentEntity> argumentEntities = new ArrayList<>();

  public ProfileElementEntity() {}

  public ProfileElementEntity(
      String name,
      String codename,
      String condition,
      String action,
      String option,
      Integer position,
      ProfileEntity profileEntity) {
    this.name = name;
    this.codename = codename;
    this.condition = condition;
    this.action = action;
    this.option = option;
    this.position = position;
    this.profileEntity = profileEntity;
  }

  public ProfileElementEntity(
      String name,
      String codename,
      String condition,
      String action,
      String option,
      List<ArgumentEntity> argumentEntities,
      Integer position,
      ProfileEntity profileEntity) {
    this.name = name;
    this.codename = codename;
    this.condition = condition;
    this.action = action;
    this.option = option;
    this.argumentEntities = argumentEntities;
    this.position = position;
    this.profileEntity = profileEntity;
  }

  public void addIncludedTag(IncludedTagEntity includedtag) {
    this.includedTagEntities.add(includedtag);
  }

  public void addExceptedtags(ExcludedTagEntity exceptedtags) {
    this.excludedTagEntities.add(exceptedtags);
  }

  public void addArgument(ArgumentEntity argumentEntity) {
    this.argumentEntities.add(argumentEntity);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCodename() {
    return codename;
  }

  public void setCodename(String codename) {
    this.codename = codename;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getOption() {
    return option;
  }

  public void setOption(String option) {
    this.option = option;
  }

  public List<ArgumentEntity> getArgumentEntities() {
    return argumentEntities;
  }

  public void setArgumentEntities(List<ArgumentEntity> argumentEntities) {
    this.argumentEntities = argumentEntities;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public ProfileEntity getProfileEntity() {
    return profileEntity;
  }

  public void setProfileEntity(ProfileEntity profileEntity) {
    this.profileEntity = profileEntity;
  }

  @JsonProperty("tags")
  public List<IncludedTagEntity> getIncludedTagEntities() {
    return includedTagEntities;
  }

  public void setIncludedTagEntities(List<IncludedTagEntity> includedTagEntities) {
    this.includedTagEntities = includedTagEntities;
  }

  @JsonProperty("excludedTags")
  public List<ExcludedTagEntity> getExcludedTagEntities() {
    return excludedTagEntities;
  }

  public void setExcludedTagEntities(List<ExcludedTagEntity> excludedTagEntities) {
    this.excludedTagEntities = excludedTagEntities;
  }
}
