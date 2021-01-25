/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DestinationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class DestinationService extends ListDataProvider<DestinationEntity> {

  // Repositories
  private final DestinationRepo destinationRepo;

  // Services
  private final ForwardNodeService forwardNodeService;
  private final KheopsAlbumsService kheopsAlbumsService;

  private ForwardNodeEntity forwardNodeEntity; // Current forward node
  private boolean hasChanges;

  /** Text filter that can be changed separately. */
  private String filterText = "";

  @Autowired
  public DestinationService(
      final DestinationRepo destinationRepo,
      final ForwardNodeService forwardNodeService,
      final KheopsAlbumsService kheopsAlbumsService) {
    super(new HashSet<>());
    this.destinationRepo = destinationRepo;
    this.forwardNodeService = forwardNodeService;
    this.kheopsAlbumsService = kheopsAlbumsService;
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

  public void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
    this.forwardNodeEntity = forwardNodeEntity;
    Collection<DestinationEntity> destinationEntities =
        this.forwardNodeService.getAllDestinations(forwardNodeEntity);

    getItems().clear();
    getItems().addAll(destinationEntities);

    hasChanges = false;
  }

  /**
   * Store given Destination to the backing destinationEntity service.
   *
   * @param destinationEntity the updated or new destinationEntity
   */
  public void save(DestinationEntity destinationEntity) {
    DestinationEntity dataUpdated =
        forwardNodeService.updateDestination(forwardNodeEntity, destinationEntity);
    if (destinationEntity.getId() == null) {
      refreshAll();
    } else {
      dataUpdated = removeValuesOnDisabledDesidentification(destinationEntity);
      refreshItem(dataUpdated);
    }
    hasChanges = true;
    destinationRepo.saveAndFlush(dataUpdated);
    kheopsAlbumsService.updateSwitchingAlbumsFromDestination(destinationEntity);
  }

  private DestinationEntity removeValuesOnDisabledDesidentification(
      DestinationEntity destinationEntity) {
    if (!destinationEntity.isDesidentification()) {
      destinationEntity.setProjectEntity(null);
    }
    return destinationEntity;
  }

  /**
   * Delete given data from the backing data service.
   *
   * @param data the data to be deleted
   */
  public void delete(DestinationEntity data) {
    forwardNodeService.deleteDestination(forwardNodeEntity, data);
    refreshAll();
    destinationRepo.deleteById(data.getId());
    // TODO: Le jours où la suprresion d'une destination se passera correctement SUPPRIMER cette
    // ligne
    data.setKheopsAlbumEntities(null);
    data.setProjectEntity(null);
    destinationRepo.saveAndFlush(data);
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

    final String filterText = filterTextInput.trim();

    if (Objects.equals(this.filterText, filterText)) {
      return;
    }
    this.filterText = filterText;

    setFilter(data -> matchesFilter(data, filterText));
  }

  private boolean matchesFilter(DestinationEntity data, String filterText) {
    return data != null && data.matchesFilter(filterText);
  }

  public boolean hasChanges() {
    return hasChanges;
  }
}
