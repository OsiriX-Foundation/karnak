/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.DestinationEntity;

public class GridDestination extends Grid<DestinationEntity> {

  public GridDestination() {
    setSizeFull();

    addColumn(DestinationEntity::getDescription)
        .setHeader("Description")
        .setFlexGrow(20)
        .setSortable(true);

    addColumn(DestinationEntity::getType).setHeader("Type").setFlexGrow(20).setSortable(true);
  }

  public DestinationEntity getSelectedRow() {
    return asSingleSelect().getValue();
  }

  public void refresh(DestinationEntity data) {
    getDataCommunicator().refresh(data);
  }
}
