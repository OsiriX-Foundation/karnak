/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.ForwardNodeEntity;

public class GridForwardNode extends Grid<ForwardNodeEntity> {

  public GridForwardNode() {
    setSizeFull();

    addColumn(ForwardNodeEntity::getFwdAeTitle)
        .setHeader("Forward AETitle")
        .setFlexGrow(20)
        .setSortable(true);

    addColumn(ForwardNodeEntity::getDescription)
        .setHeader("Description")
        .setFlexGrow(20)
        .setSortable(true);
  }

  public ForwardNodeEntity getSelectedRow() {
    return asSingleSelect().getValue();
  }

  public void refresh(ForwardNodeEntity data) {
    getDataCommunicator().refresh(data);
  }

  public void selectRow(ForwardNodeEntity row) {
    getSelectionModel().select(row);
  }
}
