/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.backend.data.repo.TransferStatusRepo;
import org.karnak.backend.data.repo.specification.TransferStatusSpecification;
import org.karnak.backend.model.event.TransferMonitoringEvent;
import org.karnak.frontend.monitoring.component.TransferStatusFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** Handle transfer monitoring */
@Service
public class TransferMonitoringService {

  @Value("${monitoring.max-history}")
  private int sizeLimit;

  // Repositories
  private final TransferStatusRepo transferStatusRepo;

  @Autowired
  public TransferMonitoringService(final TransferStatusRepo transferStatusRepo) {
    this.transferStatusRepo = transferStatusRepo;
  }

  /**
   * Listener on TransferMonitoringEvent: event is saved when received
   *
   * @param transferMonitoringEvent TransferMonitoringEvent to save
   */
  @Async
  @EventListener
  public void onTransferMonitoringEvent(TransferMonitoringEvent transferMonitoringEvent) {
    TransferStatusEntity transferStatusEntity =
        (TransferStatusEntity) transferMonitoringEvent.getSource();
    transferStatusRepo.save(transferStatusEntity);
  }

  /**
   * Retrieve transfer status depending on filter and pageable
   *
   * @param filter Filter to evaluate
   * @param pageable Pageable to evaluate
   * @return Transfer status entities found
   */
  public Page<TransferStatusEntity> retrieveTransferStatus(
      TransferStatusFilter filter, Pageable pageable) {
    Page<TransferStatusEntity> transferStatusFound;
    if (!filter.hasFilter()) {
      // No filter
      transferStatusFound = transferStatusRepo.findAll(pageable);
    } else {
      // Create the specification and query the transfer status table
      Specification<TransferStatusEntity> transferStatusSpecification =
          new TransferStatusSpecification(filter);
      transferStatusFound = transferStatusRepo.findAll(transferStatusSpecification, pageable);
    }
    return transferStatusFound;
  }

  /**
   * Count transfer status depending on filter
   *
   * @param filter Filter to evaluate
   * @return Count of Transfer status entities found
   */
  public int countTransferStatus(TransferStatusFilter filter) {
    int countTransferStatus;

    if (!filter.hasFilter()) {
      // No filter
      countTransferStatus = (int) transferStatusRepo.count();
    } else {
      // Create the specification and query the transfer status table
      Specification<TransferStatusEntity> transferStatusSpecification =
          new TransferStatusSpecification(filter);
      countTransferStatus = (int) transferStatusRepo.count(transferStatusSpecification);
    }
    return countTransferStatus;
  }

  /**
   * Occurs every 30 min: clean transfer_status table if over the size limit LIFO: clean oldest
   * records
   */
  @Scheduled(fixedRate = 30 * 60 * 1000)
  public void cleanTransferStatus() {
    int nbRecords = (int) transferStatusRepo.count();
    if (nbRecords > sizeLimit) {
      Pageable pageable = PageRequest.of(0, nbRecords - sizeLimit);
      transferStatusRepo.deleteAll(transferStatusRepo.findAllByOrderByTransferDateAsc(pageable));
    }
  }
}
