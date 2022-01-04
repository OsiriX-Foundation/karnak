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
import org.karnak.backend.model.event.TransferMonitoringEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Handle transfer monitoring */
@Service
public class TransferMonitoringService {

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
}
