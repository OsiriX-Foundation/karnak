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
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.DestinationService;
import org.karnak.frontend.component.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewUpdateDestination extends VerticalLayout {

  private final DestinationService destinationService;
  private ViewLogic viewLogic;
  private final FormDICOM formDICOM;
  private final FormSTOW formSTOW;
  private final Binder<DestinationEntity> binderFormDICOM;
  private final Binder<DestinationEntity> binderFormSTOW;
  private final ButtonSaveDeleteCancel buttonDestinationDICOMSaveDeleteCancel;
  private final ButtonSaveDeleteCancel buttonDestinationSTOWSaveDeleteCancel;
  private DestinationEntity currentDestinationEntity;

  @Autowired
    public NewUpdateDestination(DestinationService destinationService,
        FormDICOM formDICOM, FormSTOW formSTOW) {
    setSizeFull();

    this.formDICOM = formDICOM;
        this.formSTOW = formSTOW;
        this.destinationService = destinationService;
        this.binderFormDICOM = new BeanValidationBinder<>(DestinationEntity.class);
        this.binderFormSTOW = new BeanValidationBinder<>(DestinationEntity.class);
        this.buttonDestinationDICOMSaveDeleteCancel = new ButtonSaveDeleteCancel();
        this.buttonDestinationSTOWSaveDeleteCancel = new ButtonSaveDeleteCancel();
        this.currentDestinationEntity = null;
        this.formDICOM.init(binderFormDICOM, buttonDestinationDICOMSaveDeleteCancel);
        this.formSTOW.init(binderFormSTOW, buttonDestinationSTOWSaveDeleteCancel);
        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

  public void load(DestinationEntity destinationEntity, DestinationType type) {
    if (destinationEntity != null) {
      currentDestinationEntity = destinationEntity;
      buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(true);
      buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(true);
    } else {
      currentDestinationEntity =
          type == DestinationType.stow
              ? DestinationEntity.ofStowEmpty()
              : DestinationEntity.ofDicomEmpty();
      buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(false);
      buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(false);
    }
    setView(type);
  }

  public void setView(DestinationType type) {
    removeAll();
    if (type == DestinationType.stow) {
      add(formSTOW);
      binderFormSTOW.readBean(currentDestinationEntity);
    } else if (type == DestinationType.dicom) {
      add(formDICOM);
      binderFormDICOM.readBean(currentDestinationEntity);
    }
  }

  private void setButtonSaveEvent() {
    buttonDestinationDICOMSaveDeleteCancel
        .getSave()
        .addClickListener(
            event -> {
              if (currentDestinationEntity.getDestinationType() == DestinationType.dicom

                  && binderFormDICOM.writeBeanIfValid(currentDestinationEntity)) {
                NodeEventType nodeEventType =
                    currentDestinationEntity.getId() == null
                        ? NodeEventType.ADD
                        : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
              }
            });

    buttonDestinationSTOWSaveDeleteCancel
        .getSave()
        .addClickListener(
            event -> {
              if (currentDestinationEntity.getDestinationType() == DestinationType.stow

                  && binderFormSTOW.writeBeanIfValid(currentDestinationEntity)) {
                NodeEventType nodeEventType =
                    currentDestinationEntity.getId() == null
                        ? NodeEventType.ADD
                        : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
              }
            });
  }

  private void saveCurrentDestination(NodeEventType nodeEventType) {
    destinationService.save(currentDestinationEntity);
    viewLogic.updateForwardNodeInEditView();
    viewLogic
        .getApplicationEventPublisher()
        .publishEvent(new NodeEvent(currentDestinationEntity, nodeEventType));
  }

  private void setButtonDeleteEvent() {
    buttonDestinationDICOMSaveDeleteCancel
        .getDelete()
        .addClickListener(
            event -> {
              removeCurrentDestination();
            });
    buttonDestinationSTOWSaveDeleteCancel
        .getDelete()
        .addClickListener(
            event -> {
              removeCurrentDestination();
            });
  }

  private void removeCurrentDestination() {
    if (currentDestinationEntity != null) {
      ConfirmDialog dialog =
          new ConfirmDialog(
              "Are you sure to delete the forward node "
                  + currentDestinationEntity.getDescription()
                  + " ["
                  + currentDestinationEntity.getDestinationType()
                  + "] ?");
      dialog.addConfirmationListener(
          componentEvent -> {
            NodeEvent nodeEvent = new NodeEvent(currentDestinationEntity, NodeEventType.REMOVE);
            destinationService.delete(currentDestinationEntity);
            viewLogic.getApplicationEventPublisher().publishEvent(nodeEvent);
            viewLogic.updateForwardNodeInEditView();
          });
      dialog.open();
    }
  }

  public Button getButtonDICOMCancel() {
    return buttonDestinationDICOMSaveDeleteCancel.getCancel();
  }

  public Button getButtonSTOWCancel() {
    return buttonDestinationSTOWSaveDeleteCancel.getCancel();
  }

    public ViewLogic getViewLogic() {
        return viewLogic;
    }

    public void setViewLogic(ViewLogic viewLogic) {
        this.viewLogic = viewLogic;
    }
}
