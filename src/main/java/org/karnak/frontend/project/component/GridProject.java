/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import java.util.Arrays;
import org.karnak.backend.data.entity.ProjectEntity;

public class GridProject extends Grid<ProjectEntity> {

  public GridProject() {
    setWidthFull();
    setHeightByRows(true);

    Column<ProjectEntity> projectNameColumn =
        addColumn(ProjectEntity::getName)
            .setHeader("Project Name")
            .setFlexGrow(15)
            .setSortable(true);
    addColumn(project -> project.getProfileEntity().getName())
        .setHeader("Desidenfication profile")
        .setFlexGrow(15)
        .setSortable(true);

    // Set by default the order on the name of the column
    GridSortOrder<ProjectEntity> order =
        new GridSortOrder<>(projectNameColumn, SortDirection.ASCENDING);
    sort(Arrays.asList(order));
  }

  public void selectRow(ProjectEntity row) {
    if (row != null) {
      getSelectionModel().select(row);
    } else {
      getSelectionModel().deselectAll();
    }
  }
}
