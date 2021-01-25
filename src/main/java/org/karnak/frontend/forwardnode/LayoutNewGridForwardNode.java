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

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.ForwardNodeAPIService;

@UIScope
public class LayoutNewGridForwardNode extends VerticalLayout {

  private final ForwardNodeViewLogic forwardNodeViewLogic;
  private final ForwardNodeAPIService forwardNodeAPIService;

  private final NewForwardNode newForwardNode;
  private final GridForwardNode gridForwardNode;

  private final Button buttonNewForwardNode;
  private final TextField textFieldNewAETitleForwardNode;
  private final Button buttonAddNewForwardNode;
  private Button buttonCancelNewForwardNode;

  public LayoutNewGridForwardNode(
      ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPIService forwardNodeAPIService) {
    this.forwardNodeViewLogic = forwardNodeViewLogic;
    this.forwardNodeAPIService = forwardNodeAPIService;
    newForwardNode = new NewForwardNode();
    gridForwardNode = new GridForwardNode();
    gridForwardNode.setDataProvider(forwardNodeAPIService.getDataProvider());
    add(newForwardNode, gridForwardNode);

    buttonNewForwardNode = newForwardNode.getNewForwardNode();
    textFieldNewAETitleForwardNode = newForwardNode.getNewAETitleForwardNode();
    buttonAddNewForwardNode = newForwardNode.getAddNewForwardNode();

    eventNewForwardNode();
    eventGridSelection();
  }

  public void load(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null && forwardNodeEntity != gridForwardNode.getSelectedRow()) {
      gridForwardNode.selectRow(forwardNodeEntity);
    } else {
      gridForwardNode.getSelectionModel().deselectAll();
    }
  }

  private void eventNewForwardNode() {
    buttonAddNewForwardNode.addClickListener(
        click -> {
          eventAddForwardNode(new ForwardNodeEntity(textFieldNewAETitleForwardNode.getValue()));
        });
    textFieldNewAETitleForwardNode.addKeyDownListener(
        Key.ENTER,
        keyDownEvent -> {
          eventAddForwardNode(new ForwardNodeEntity(textFieldNewAETitleForwardNode.getValue()));
        });
  }

  private void eventAddForwardNode(ForwardNodeEntity forwardNodeEntity) {
    forwardNodeAPIService.addForwardNode(forwardNodeEntity);
    gridForwardNode.getSelectionModel().select(forwardNodeEntity);
    forwardNodeViewLogic.editForwardNode(forwardNodeEntity);
  }

  private void eventGridSelection() {
    gridForwardNode
        .asSingleSelect()
        .addValueChangeListener(
            event -> {
              forwardNodeViewLogic.editForwardNode(event.getValue());
            });
  }
}
