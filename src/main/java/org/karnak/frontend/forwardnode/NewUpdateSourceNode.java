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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.SourceNodeDataProvider;
import org.karnak.frontend.component.ConfirmDialog;

public class NewUpdateSourceNode extends VerticalLayout {

  private final Binder<DicomSourceNodeEntity> binderFormSourceNode;
  private final SourceNodeDataProvider dataProvider;
  private final ViewLogic viewLogic;
  private final FormSourceNode formSourceNode;
  private final ButtonSaveDeleteCancel buttonSaveDeleteCancel;
  private DicomSourceNodeEntity currentSourceNode;

  public NewUpdateSourceNode(SourceNodeDataProvider sourceNodeDataProvider, ViewLogic viewLogic) {
    currentSourceNode = null;
    dataProvider = sourceNodeDataProvider;
    this.viewLogic = viewLogic;
    binderFormSourceNode = new BeanValidationBinder<>(DicomSourceNodeEntity.class);
    buttonSaveDeleteCancel = new ButtonSaveDeleteCancel();
    formSourceNode = new FormSourceNode(binderFormSourceNode, buttonSaveDeleteCancel);

    setButtonSaveEvent();
    setButtonDeleteEvent();
  }

  public void setView() {
    removeAll();
    binderFormSourceNode.readBean(currentSourceNode);
    add(formSourceNode);
  }

  public void load(DicomSourceNodeEntity sourceNode) {
    if (sourceNode != null) {
      currentSourceNode = sourceNode;
      buttonSaveDeleteCancel.getDelete().setEnabled(true);
    } else {
      currentSourceNode = DicomSourceNodeEntity.ofEmpty();
      buttonSaveDeleteCancel.getDelete().setEnabled(false);
    }
    setView();
  }

  private void setButtonSaveEvent() {
    buttonSaveDeleteCancel
        .getSave()
        .addClickListener(
            event -> {
              NodeEventType nodeEventType =
                  currentSourceNode.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
              if (binderFormSourceNode.writeBeanIfValid(currentSourceNode)) {
                dataProvider.save(currentSourceNode);
                viewLogic.updateForwardNodeInEditView();
                viewLogic
                    .getApplicationEventPublisher()
                    .publishEvent(new NodeEvent(currentSourceNode, nodeEventType));
              }
            });
  }

  private void setButtonDeleteEvent() {
    buttonSaveDeleteCancel
        .getDelete()
        .addClickListener(
            event -> {
              if (currentSourceNode != null) {
                ConfirmDialog dialog =
                    new ConfirmDialog(
                        "Are you sure to delete the DICOM source node "
                            + currentSourceNode.getAeTitle()
                            + "?");
                dialog.addConfirmationListener(
                    componentEvent -> {
                      NodeEvent nodeEvent = new NodeEvent(currentSourceNode, NodeEventType.REMOVE);
                      dataProvider.delete(currentSourceNode);
                      viewLogic.updateForwardNodeInEditView();
                      viewLogic.getApplicationEventPublisher().publishEvent(nodeEvent);
                    });
                dialog.open();
              }
            });
  }

  public Button getButtonCancel() {
    return buttonSaveDeleteCancel.getCancel();
  }
}
