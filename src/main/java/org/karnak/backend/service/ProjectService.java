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
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.data.repo.ProjectRepo;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends ListDataProvider<ProjectEntity> {

  // Repositories
    private final ProjectRepo projectRepo;

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ProjectService(final ProjectRepo projectRepo) {
        super(new ArrayList<>());
        this.projectRepo = projectRepo;
        getItems().addAll(getAllProjects());
    }

  public void save(ProjectEntity projectEntity) {
    boolean isNewProject = projectEntity.getId() == null;
    if (isNewProject) {
      getItems().add(projectEntity);
    } else {
      refreshItem(projectEntity);
    }
    projectRepo.saveAndFlush(projectEntity);
    refreshAll();
  }

  public void update(ProjectEntity projectEntity) {
    if (projectEntity.getId() != null) {
      projectRepo.saveAndFlush(projectEntity);
      updateDestinations(projectEntity);
      refreshAll();
    }
  }

  private void updateDestinations(ProjectEntity projectEntity) {
    for (DestinationEntity destinationEntity : projectEntity.getDestinationEntities()) {
      applicationEventPublisher.publishEvent(
          new NodeEvent(destinationEntity, NodeEventType.UPDATE));
    }
  }

  public void remove(ProjectEntity projectEntity) {
    projectRepo.deleteById(projectEntity.getId());
    projectRepo.flush();
    refreshAll();
  }

  public ProjectEntity getProjectById(Long projectID) {
    refreshAll();
    return getItems().stream()
        .filter(project -> project.getId().equals(projectID))
        .findAny()
        .orElse(null);
  }

  public List<ProjectEntity> getAllProjects() {
    return projectRepo.findAll();
  }

  public ApplicationEventPublisher getApplicationEventPublisher() {
    return applicationEventPublisher;
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    getItems().addAll(getAllProjects());
    super.refreshAll();
  }
}
