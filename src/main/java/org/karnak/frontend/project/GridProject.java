/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.project;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectDataProvider;

public class GridProject extends Grid<ProjectEntity> {

  private final ProjectDataProvider projectDataProvider;

  public GridProject(ProjectDataProvider projectDataProvider) {
    this.projectDataProvider = projectDataProvider;
    setDataProvider(this.projectDataProvider);
    setWidthFull();
    setHeightByRows(true);

    addColumn(ProjectEntity::getName).setHeader("Project Name").setFlexGrow(15).setSortable(true);
    addColumn(project -> project.getProfileEntity().getName())
        .setHeader("Desidenfication profile")
        .setFlexGrow(15)
        .setSortable(true);
  }

  public void selectRow(ProjectEntity row) {
    if (row != null) {
      getSelectionModel().select(row);
    } else {
      getSelectionModel().deselectAll();
    }
  }
}
