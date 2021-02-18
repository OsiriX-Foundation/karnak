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

import java.util.Collection;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.repo.DestinationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** Service managing destinations */
@Service
public class DestinationService {

  // Repositories
  private final DestinationRepo destinationRepo;

  // Services
  private final ForwardNodeService forwardNodeService;
  private final KheopsAlbumsService kheopsAlbumsService;

  // Event publisher
  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Autowired constructor
   *
   * @param destinationRepo Destination repository
   * @param forwardNodeService ForwardNode Service
   * @param kheopsAlbumsService Kheops Albums Service
   * @param applicationEventPublisher ApplicationEventPublisher
   */
  @Autowired
  public DestinationService(
      final DestinationRepo destinationRepo,
      final ForwardNodeService forwardNodeService,
      final KheopsAlbumsService kheopsAlbumsService,
      final ApplicationEventPublisher applicationEventPublisher) {
    this.destinationRepo = destinationRepo;
    this.forwardNodeService = forwardNodeService;
    this.kheopsAlbumsService = kheopsAlbumsService;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  /**
   * Store given Destination to the backing destinationEntity service.
   *
   * @param forwardNodeEntity
   * @param destinationEntity the updated or new destinationEntity
   */
  public DestinationEntity save(
      ForwardNodeEntity forwardNodeEntity, DestinationEntity destinationEntity) {
    DestinationEntity dataUpdated =
        forwardNodeService.updateDestination(forwardNodeEntity, destinationEntity);

    if (destinationEntity.getId() != null) {
      dataUpdated = removeValuesOnDisabledDesidentification(destinationEntity);
    }
    destinationRepo.saveAndFlush(dataUpdated);
    kheopsAlbumsService.updateSwitchingAlbumsFromDestination(destinationEntity);
    return dataUpdated;
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
   * @param forwardNodeEntity
   * @param data the data to be deleted
   */
  public void delete(ForwardNodeEntity forwardNodeEntity, DestinationEntity data) {
    forwardNodeService.deleteDestination(forwardNodeEntity, data);
    destinationRepo.deleteById(data.getId());
    // TODO: Le jours où la suprresion d'une destination se passera correctement SUPPRIMER cette
    // ligne
    data.setKheopsAlbumEntities(null);
    data.setProjectEntity(null);
    destinationRepo.saveAndFlush(data);
  }

  public ApplicationEventPublisher getApplicationEventPublisher() {
    return applicationEventPublisher;
  }

  /**
   * Retrieve destinations of a forward node
   *
   * @param forwardNodeEntity forward node
   * @return destinations found
   */
  public Collection<DestinationEntity> retrieveDestinations(ForwardNodeEntity forwardNodeEntity) {
    return forwardNodeService.getAllDestinations(forwardNodeEntity);
  }
}
