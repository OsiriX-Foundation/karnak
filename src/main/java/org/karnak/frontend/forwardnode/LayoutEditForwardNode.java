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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.service.DestinationService;
import org.karnak.backend.service.ForwardNodeAPIService;
import org.karnak.backend.service.SourceNodeService;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@org.springframework.stereotype.Component
@UIScope
public class LayoutEditForwardNode extends VerticalLayout {

  private final ForwardNodeViewLogic forwardNodeViewLogic;
  private final ViewLogic viewLogic;
  private final ForwardNodeAPIService forwardNodeAPIService;
  private final Binder<ForwardNodeEntity> binderForwardNode;
  private final DestinationService destinationService;
  private final SourceNodeService sourceNodeService;
  private final EditAETitleDescription editAETitleDescription;
  private final TabSourcesDestination tabSourcesDestination;
  private final VerticalLayout layoutDestinationsSources;
  private final DestinationsView destinationsView;
  private final SourceNodesView sourceNodesView;
  private final NewUpdateDestination newUpdateDestination;
  private final ButtonSaveDeleteCancel buttonForwardNodeSaveDeleteCancel;
  private final NewUpdateSourceNode newUpdateSourceNode;
  public ForwardNodeEntity currentForwardNodeEntity;
  private ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public LayoutEditForwardNode(
      ForwardNodeViewLogic forwardNodeViewLogic,
      ForwardNodeAPIService forwardNodeAPIService,
      NewUpdateDestination newUpdateDestination,
      NewUpdateSourceNode newUpdateSourceNode,
      SourceNodeService sourceNodeService,
      SourceNodesView sourceNodesView,
      DestinationService destinationService,
      DestinationsView destinationsView,
      final ApplicationEventPublisher applicationEventPublisher) {

    this.forwardNodeViewLogic = forwardNodeViewLogic;
    this.viewLogic = new ViewLogic(this);
    this.viewLogic.setApplicationEventPublisher(applicationEventPublisher);
    this.forwardNodeAPIService = forwardNodeAPIService;
    this.currentForwardNodeEntity = null;
    this.binderForwardNode = new BeanValidationBinder<>(ForwardNodeEntity.class);
    this.tabSourcesDestination = new TabSourcesDestination();
    this.layoutDestinationsSources = new VerticalLayout();
    this.buttonForwardNodeSaveDeleteCancel = new ButtonSaveDeleteCancel();
    this.newUpdateDestination = newUpdateDestination;
    this.newUpdateSourceNode = newUpdateSourceNode;
    this.sourceNodeService = sourceNodeService;
    this.sourceNodesView = sourceNodesView;
    this.destinationService = destinationService;
    this.destinationsView = destinationsView;

    this.layoutDestinationsSources.setSizeFull();
    this.editAETitleDescription = new EditAETitleDescription(binderForwardNode);

    this.newUpdateDestination.setViewLogic(this.viewLogic);
    this.newUpdateSourceNode.setViewLogic(this.viewLogic);

    getStyle().set("overflow-y", "auto");
    setSizeFull();
    setEditView();
    setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
    setEventChangeTabValue();
    setEventCancelButton();
    setEventDeleteButton();
    setEventSaveButton();
    setEventBinderForwardNode();
    setEventDestination();
    setEventDestinationsViewDICOM();
    setEventDestinationsViewSTOW();
    setEventDestinationCancelButton();
    setEventNewSourceNode();
    setEventGridSourceNode();
    setEventSourceNodeCancelButton();
  }

  public void setEditView() {
    removeAll();
    add(
        UIS.setWidthFull(this.editAETitleDescription),
        UIS.setWidthFull(this.tabSourcesDestination),
        UIS.setWidthFull(this.layoutDestinationsSources),
        UIS.setWidthFull(this.buttonForwardNodeSaveDeleteCancel));
  }

  public void load(ForwardNodeEntity forwardNodeEntity) {
    this.currentForwardNodeEntity = forwardNodeEntity;
    this.editAETitleDescription.setForwardNode(forwardNodeEntity);
    setApplicationEventPublisher(forwardNodeAPIService.getApplicationEventPublisher());
    this.destinationsView.setForwardNode(forwardNodeEntity);
    this.destinationService.setForwardNode(forwardNodeEntity);

    this.sourceNodesView.setForwardNode(forwardNodeEntity);
    this.sourceNodeService.setForwardNode(forwardNodeEntity);

    setEditView();
    if (forwardNodeEntity == null) {
      this.tabSourcesDestination.setEnabled(false);
      this.buttonForwardNodeSaveDeleteCancel.setEnabled(false);
    } else {
      this.tabSourcesDestination.setEnabled(true);
      this.buttonForwardNodeSaveDeleteCancel.setEnabled(true);
    }
  }

  private void setEventChangeTabValue() {
    this.tabSourcesDestination.addSelectedChangeListener(
        event -> {
          Tab selectedTab = event.getSource().getSelectedTab();
          setLayoutDestinationsSources(selectedTab.getLabel());
        });
  }

  private void setLayoutDestinationsSources(String currentTab) {
    this.layoutDestinationsSources.removeAll();
    if (currentTab.equals(this.tabSourcesDestination.LABEL_SOURCES)) {
      this.layoutDestinationsSources.add(this.sourceNodesView);
    } else if (currentTab.equals(this.tabSourcesDestination.LABEL_DESTINATIONS)) {
      this.layoutDestinationsSources.add(this.destinationsView);
    }
  }

  private void setEventDestinationsViewDICOM() {
    this.destinationsView
        .getNewDestinationDICOM()
        .addClickListener(
            event -> {
              this.newUpdateDestination.load(null, DestinationType.dicom);
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventDestinationsViewSTOW() {
    this.destinationsView
        .getNewDestinationSTOW()
        .addClickListener(
            event -> {
              this.newUpdateDestination.load(null, DestinationType.stow);
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventNewSourceNode() {
    this.sourceNodesView
        .getNewSourceNode()
        .addClickListener(
            event -> {
              this.newUpdateSourceNode.load(null);
              addFormView(this.newUpdateSourceNode);
            });
  }

  private void addFormView(Component form) {
    removeAll();
    add(form);
  }

  private void setEventCancelButton() {
    this.buttonForwardNodeSaveDeleteCancel
        .getCancel()
        .addClickListener(
            event -> {
              this.forwardNodeViewLogic.cancelForwardNode();
            });
  }

  private void setEventDeleteButton() {
    this.buttonForwardNodeSaveDeleteCancel
        .getDelete()
        .addClickListener(
            event -> {
              if (this.currentForwardNodeEntity != null) {
                ConfirmDialog dialog =
                    new ConfirmDialog(
                        "Are you sure to delete the forward node "
                            + this.currentForwardNodeEntity.getFwdAeTitle()
                            + " ?");
                dialog.addConfirmationListener(
                    componentEvent -> {
                      this.forwardNodeAPIService.deleteForwardNode(this.currentForwardNodeEntity);
                      this.forwardNodeViewLogic.cancelForwardNode();
                    });
                dialog.open();
              }
            });
  }

  private void setEventBinderForwardNode() {
    this.binderForwardNode.addStatusChangeListener(
        event -> {
          boolean isValid = !event.hasValidationErrors();
          boolean hasChanges = this.binderForwardNode.hasChanges();
          this.buttonForwardNodeSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
  }

  private void setEventSaveButton() {
    this.buttonForwardNodeSaveDeleteCancel
        .getSave()
        .addClickListener(
            event -> {
              if (binderForwardNode.writeBeanIfValid(this.currentForwardNodeEntity)) {
                this.forwardNodeAPIService.updateForwardNode(this.currentForwardNodeEntity);
                this.forwardNodeViewLogic.cancelForwardNode();
              }
            });
  }

  private void setEventDestination() {
    this.destinationsView
        .getGridDestination()
        .addItemClickListener(
            event -> {
              DestinationEntity destinationEntity = event.getItem();
              this.newUpdateDestination.load(
                  destinationEntity, destinationEntity.getDestinationType());
              addFormView(this.newUpdateDestination);
            });
  }

  private void setEventDestinationCancelButton() {
    this.newUpdateDestination
        .getButtonDICOMCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
    this.newUpdateDestination
        .getButtonSTOWCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
  }

  private void setEventGridSourceNode() {
    this.sourceNodesView
        .getGridSourceNode()
        .addItemClickListener(
            event -> {
              DicomSourceNodeEntity dicomSourceNodeEntity = event.getItem();
              this.newUpdateSourceNode.load(dicomSourceNodeEntity);
              addFormView(this.newUpdateSourceNode);
            });
  }

  private void setEventSourceNodeCancelButton() {
    this.newUpdateSourceNode
        .getButtonCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
  }

  public ApplicationEventPublisher getApplicationEventPublisher() {
    return applicationEventPublisher;
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void updateForwardNodeInEditView() {
    this.load(currentForwardNodeEntity);
  }
}
