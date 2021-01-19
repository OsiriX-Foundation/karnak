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

@Entity(name = "KheopsAlbums")
@Table(name = "kheops_albums")
public class KheopsAlbumsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String urlAPI;
  private String authorizationDestination;
  private String authorizationSource;
  private String condition;

  @ManyToOne
  @JoinColumn(name = "destination_id")
  private DestinationEntity destinationEntity = new DestinationEntity();

  public KheopsAlbumsEntity() {}

  public KheopsAlbumsEntity(
      String urlAPI,
      String authorizationDestination,
      String authorizationSource,
      String condition) {
    this.urlAPI = urlAPI;
    this.authorizationDestination = authorizationDestination;
    this.authorizationSource = authorizationSource;
    this.condition = condition;
  }

  public Long getId() {
    return id;
  }

  public String getUrlAPI() {
    return urlAPI;
  }

  public void setUrlAPI(String urlAPI) {
    this.urlAPI = urlAPI;
  }

  public String getAuthorizationDestination() {
    return authorizationDestination;
  }

  public void setAuthorizationDestination(String authorizationDestination) {
    this.authorizationDestination = authorizationDestination;
  }

  public String getAuthorizationSource() {
    return authorizationSource;
  }

  public void setAuthorizationSource(String authorizationSource) {
    this.authorizationSource = authorizationSource;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public DestinationEntity getDestinationEntity() {
    return destinationEntity;
  }

  public void setDestinationEntity(DestinationEntity destinationEntity) {
    this.destinationEntity = destinationEntity;
  }
}
