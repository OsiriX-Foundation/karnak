/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import org.karnak.backend.data.entity.DestinationEntity;

public class GridDestination extends Grid<DestinationEntity> {

  public GridDestination() {
    setSizeFull();

    addColumn(DestinationEntity::getDescription)
        .setHeader("Description")
        .setFlexGrow(20)
        .setSortable(true);

    addColumn(DestinationEntity::getDestinationType)
        .setHeader("Type")
        .setFlexGrow(20)
        .setSortable(true);

    addComponentColumn(
            destination -> {
              Span spanDot = new Span();
              spanDot.getStyle().set("height", "25px");
              spanDot.getStyle().set("width", "25px");
              spanDot.getStyle().set("border-radius", "50%");
              spanDot.getStyle().set("display", "inline-block");
              if (destination.isActivate()) {
                spanDot.getStyle().set("background-color", "#5FC04C");
              } else {
                spanDot.getStyle().set("background-color", "#FC4848");
              }
              return spanDot;
            })
        .setHeader("Enabled")
        .setFlexGrow(20)
        .setSortable(true);
  }

  public DestinationEntity getSelectedRow() {
    return asSingleSelect().getValue();
  }

  public void refresh(DestinationEntity data) {
    getDataCommunicator().refresh(data);
  }
}
