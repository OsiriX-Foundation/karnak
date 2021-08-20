/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** Logic service use to make calls to backend and implement logic linked to the view */
@Service
public class DestinationLogic extends ListDataProvider<DestinationEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DestinationLogic.class);
  public static final String TRANSFER_IN_PROGRESS = "Transfer in progress";
  public static final String SAVE = "Save";
  public static final String DELETE = "Delete";

  // View
  private DestinationView destinationView;

  // Services
  private final transient DestinationService destinationService;

  /** Text filter that can be changed separately. */
  private String filterText = "";

  private ForwardNodeEntity forwardNodeEntity; // Current forward node

  /**
   * Autowired constructor
   *
   * @param destinationService Destination Service
   */
  @Autowired
  public DestinationLogic(final DestinationService destinationService) {
    super(new HashSet<>());
    this.destinationService = destinationService;
  }

  @Override
  public Object getId(DestinationEntity data) {
    Objects.requireNonNull(data, "Cannot provide an id for a null item.");
    return data.hashCode();
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    if (forwardNodeEntity != null) {
      getItems().addAll(forwardNodeEntity.getDestinationEntities());
    }
    super.refreshAll();
  }

  /** Check activity on the forward node */
  @Scheduled(fixedRate = 1000)
  public void checkStatusTransfers() {
    if (forwardNodeEntity != null) {
      // Refreshed destinations from DB
      List<DestinationEntity> refreshedDestinations =
          destinationService.retrieveDestinationsFromIds(
              forwardNodeEntity.getDestinationEntities().stream()
                  .map(DestinationEntity::getId)
                  .collect(Collectors.toList()));

      // Loading spinner
      checkActivityLoadingSpinner(
          refreshedDestinations.stream()
              .filter(DestinationEntity::isActivate)
              .collect(Collectors.toList()));

      // Buttons save/delete enable/disable
      checkActivityEnableDisableButtons(refreshedDestinations);
    }
  }

  /**
   * Enable/disable Save and Delete buttons depending on activity on a destination. If a transfer is
   * in progress disable buttons save and delete for all destinations of the forward node (activated
   * or not)
   *
   * @param destinationEntities Refreshed destinations from DB
   */
  private void checkActivityEnableDisableButtons(List<DestinationEntity> destinationEntities) {
    // If a transfer is in progress: disable
    if (destinationEntities.stream().anyMatch(DestinationEntity::isTransferInProgress)) {
      destinationView.getUi().access(this::disableSaveDeleteButtons);
    } else {
      // If no transfer: enable
      destinationView.getUi().access(this::enableSaveDeleteButtons);
    }
  }

  /**
   * Check activity for loading spinner
   *
   * @param activatedDestinationEntities Destinations to check
   */
  private void checkActivityLoadingSpinner(List<DestinationEntity> activatedDestinationEntities) {
    activatedDestinationEntities.forEach(
        d -> {
          // Retrieve the loading image of the corresponding destination
          Image loadingImage =
              destinationView
                  .getGridDestination()
                  .getLoadingImages()
                  .get(forwardNodeEntity.getFwdAeTitle())
                  .get(d.getId());

          // Check there is some activity on the destination: if yes set the loading spinner visible
          // otherwise set it invisible
          if (d.isTransferInProgress() && !loadingImage.isVisible()) {
            // Loading spinner visible
            destinationView.getUi().access(() -> loadingImage.setVisible(true));
          } else if (!d.isTransferInProgress() && loadingImage.isVisible()) {
            // Loading spinner invisible
            destinationView.getUi().access(() -> loadingImage.setVisible(false));
          }
        });
  }

  /** Enable save delete buttons */
  private void enableSaveDeleteButtons() {
    // Forward node
    enableButtonTransferInProgress(
        destinationView.getButtonForwardNodeSaveDeleteCancel().getSave(),
        destinationView.getButtonForwardNodeSaveDeleteCancel().getDelete());

    // Disable save button editable form stow/dicom
    // Dicom
    enableButtonTransferInProgress(
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationDICOMSaveDeleteCancel()
            .getSave(),
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationDICOMSaveDeleteCancel()
            .getDelete());

    // Stow
    enableButtonTransferInProgress(
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationSTOWSaveDeleteCancel()
            .getSave(),
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationSTOWSaveDeleteCancel()
            .getDelete());
  }

  /** Disable save delete buttons */
  private void disableSaveDeleteButtons() {
    // Forward node
    disableButtonTransferInProgress(
        destinationView.getButtonForwardNodeSaveDeleteCancel().getSave(),
        destinationView.getButtonForwardNodeSaveDeleteCancel().getDelete());

    // Disable save button editable form stow/dicom
    // Dicom
    disableButtonTransferInProgress(
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationDICOMSaveDeleteCancel()
            .getSave(),
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationDICOMSaveDeleteCancel()
            .getDelete());

    // Stow
    disableButtonTransferInProgress(
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationSTOWSaveDeleteCancel()
            .getSave(),
        destinationView
            .getNewUpdateDestination()
            .getButtonDestinationSTOWSaveDeleteCancel()
            .getDelete());
  }

  /**
   * Enable buttons
   *
   * @param saveButton Save button
   * @param deleteButton Delete button
   */
  private void enableButtonTransferInProgress(Button saveButton, Button deleteButton) {
    saveButton.setEnabled(true);
    deleteButton.setEnabled(true);
    saveButton.setText(SAVE);
    deleteButton.setText(DELETE);
  }

  /**
   * Disable buttons
   *
   * @param saveButton Save button
   * @param deleteButton Delete button
   */
  private void disableButtonTransferInProgress(Button saveButton, Button deleteButton) {
    saveButton.setEnabled(false);
    deleteButton.setEnabled(false);
    saveButton.setText(TRANSFER_IN_PROGRESS);
    deleteButton.setText(TRANSFER_IN_PROGRESS);
  }

  /**
   * Sets the filter to use for this data provider and refreshes data.
   *
   * <p>Filter is compared for allowed properties.
   *
   * @param filterTextInput the text to filter by, never null.
   */
  public void setFilter(String filterTextInput) {
    Objects.requireNonNull(filterText, "Filter text cannot be null.");

    final String filterTextInputTrim = filterTextInput.trim();

    if (Objects.equals(this.filterText, filterTextInputTrim)) {
      return;
    }
    this.filterText = filterTextInputTrim;

    setFilter(data -> matchesFilter(data, filterTextInputTrim));
  }

  private boolean matchesFilter(DestinationEntity data, String filterText) {
    return data != null && data.matchesFilter(filterText);
  }

  public DestinationView getDestinationsView() {
    return destinationView;
  }

  public void setDestinationsView(DestinationView destinationView) {
    this.destinationView = destinationView;
  }

  /**
   * Load the forward node in the list of items
   *
   * @param forwardNodeEntity Forward node to load
   */
  public void loadForwardNode(ForwardNodeEntity forwardNodeEntity) {
    this.forwardNodeEntity = forwardNodeEntity;
    getItems().clear();
    getItems().addAll(destinationService.retrieveDestinations(this.forwardNodeEntity));
  }

  /**
   * Save the destination
   *
   * @param destinationEntity destination to save
   */
  public void saveDestination(DestinationEntity destinationEntity) {
    destinationService.save(forwardNodeEntity, destinationEntity);
    loadForwardNode(forwardNodeEntity);
  }

  public void publishEvent(NodeEvent nodeEvent) {
    destinationService.getApplicationEventPublisher().publishEvent(nodeEvent);
  }

  /**
   * Delete the destination in parameter
   *
   * @param destinationEntity destination to delete
   */
  public void deleteDestination(DestinationEntity destinationEntity) {
    destinationService.delete(destinationEntity);
    refreshAll();
  }
}
