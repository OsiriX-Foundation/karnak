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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.SourceNodeService;
import org.karnak.frontend.component.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewUpdateSourceNode extends VerticalLayout {

  private final Binder<DicomSourceNodeEntity> binderFormSourceNode;
  private final SourceNodeService sourceNodeService;
  private ViewLogic viewLogic;
  private final FormSourceNode formSourceNode;
  private final ButtonSaveDeleteCancel buttonSaveDeleteCancel;
  private DicomSourceNodeEntity currentSourceNode;

  @Autowired
  public NewUpdateSourceNode(SourceNodeService sourceNodeService) {
      currentSourceNode = null;
      this.sourceNodeService = sourceNodeService;
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
                        currentSourceNode.getId() == null ? NodeEventType.ADD
                            : NodeEventType.UPDATE;
                    if (binderFormSourceNode.writeBeanIfValid(currentSourceNode)) {
                        sourceNodeService.save(currentSourceNode);
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
                                NodeEvent nodeEvent = new NodeEvent(currentSourceNode,
                                    NodeEventType.REMOVE);
                                sourceNodeService.delete(currentSourceNode);
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

    public ViewLogic getViewLogic() {
        return viewLogic;
    }

    public void setViewLogic(ViewLogic viewLogic) {
        this.viewLogic = viewLogic;
    }
}
