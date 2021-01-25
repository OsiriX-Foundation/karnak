/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.List;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class ProfileNameGrid extends Grid<ProfileEntity> {

  List<ProfileEntity> profileEntities;
  private final ProfilePipeService profilePipeService;

  @Autowired
  public ProfileNameGrid(final ProfilePipeService profilePipeService) {
    this.profilePipeService = profilePipeService;
  }

  @PostConstruct
  public void init() {
    setSelectionMode(SelectionMode.SINGLE);
    updatedProfilePipesView();
    addColumn(ProfileEntity::getName).setHeader("Name");
    addColumn(ProfileEntity::getVersion).setHeader("Version");
  }

  public void updatedProfilePipesView() {
    profileEntities = profilePipeService.getAllProfiles();
    setItems(profileEntities);
  }

  public void selectRow(ProfileEntity row) {
    getSelectionModel().select(row);
  }
}
