/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile.component;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.ProfileEntity;

public class ProfileGrid extends Grid<ProfileEntity> {

  public ProfileGrid() {
    setSelectionMode(SelectionMode.SINGLE);
    addColumn(ProfileEntity::getName).setHeader("Name");
    addColumn(ProfileEntity::getVersion).setHeader("Version");
  }

  public void selectRow(ProfileEntity row) {
    if (row != null) {
      getSelectionModel().select(row);
    } else {
      getSelectionModel().deselectAll();
    }
  }
}
