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

import java.io.Serializable;
import java.util.Optional;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.springframework.context.ApplicationEventPublisher;

public class ForwardNodeAPI implements Serializable {

  private final ForwardNodeDataProvider dataProvider;
  private ApplicationEventPublisher applicationEventPublisher;

  public ForwardNodeAPI(ForwardNodeDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public ForwardNodeDataProvider getDataProvider() {
    return dataProvider;
  }

  public void addForwardNode(ForwardNodeEntity data) {
    NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
    if (eventType == NodeEventType.ADD) {
      Optional<ForwardNodeEntity> val =
          dataProvider.getDataService().getAllForwardNodes().stream()
              .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle()))
              .findFirst();
      if (val.isPresent()) {
        // showError("Cannot add this new node because the AE-Title already exists!");
        return;
      }
    }
    dataProvider.save(data);
    applicationEventPublisher.publishEvent(new NodeEvent(data, eventType));
  }

  public void updateForwardNode(ForwardNodeEntity data) {
    dataProvider.save(data);
    applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.UPDATE));
  }

  public void deleteForwardNode(ForwardNodeEntity data) {
    dataProvider.delete(data);
    applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.REMOVE));
  }

  public ForwardNodeEntity getForwardNodeById(Long dataId) {
    return dataProvider.get(dataId);
  }

  public ApplicationEventPublisher getApplicationEventPublisher() {
    return applicationEventPublisher;
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}
