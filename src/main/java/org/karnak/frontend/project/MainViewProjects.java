/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.service.ProjectService;
import org.karnak.frontend.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;

@Route(value = "projects", layout = MainLayout.class)
@PageTitle("KARNAK - Projects")
@Secured({"ADMIN"})
public class MainViewProjects extends HorizontalLayout implements HasUrlParameter<String> {

  public static final String VIEW_NAME = "Projects";

  private final ProjectService projectService;
  private final NewProjectForm newProjectForm;
  private final GridProject gridProject;
  private final EditProject editProject;
  private final Binder<ProjectEntity> newResearchBinder;

  @Autowired
  public MainViewProjects(
      final ProjectService projectService,
      final EditProject editProject,
      final NewProjectForm newProjectForm) {
    this.projectService = projectService;
    this.editProject = editProject;
    this.newProjectForm = newProjectForm;
    this.gridProject = new GridProject(projectService);
    this.newResearchBinder = this.newProjectForm.getBinder();
    VerticalLayout layoutNewProject = new VerticalLayout(this.newProjectForm, this.gridProject);
    setWidthFull();
    layoutNewProject.setWidth("40%");
    this.editProject.setWidth("60%");
    add(layoutNewProject, this.editProject);
    setEventButtonNewProject();
    setEventGridSelection();
  }

  private void setEventButtonNewProject() {
    newProjectForm
        .getButtonAdd()
        .addClickListener(
            event -> {
              ProjectEntity newProjectEntity = new ProjectEntity();
              if (newResearchBinder.writeBeanIfValid(newProjectEntity)) {
                newProjectEntity.setSecret(HMAC.generateRandomKey());
                projectService.save(newProjectEntity);
                newProjectForm.clear();
                ProjectViewLogic.navigateProject(newProjectEntity);
              }
            });
  }

  private void setEventGridSelection() {
    gridProject
        .asSingleSelect()
        .addValueChangeListener(
            event -> {
              ProjectViewLogic.navigateProject(event.getValue());
            });
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    Long idProject = ProjectViewLogic.enter(parameter);
    ProjectEntity currentProjectEntity = null;
    if (idProject != null) {
      currentProjectEntity = projectService.getProjectById(idProject);
    }
    editProject.setProject(currentProjectEntity);
    gridProject.selectRow(currentProjectEntity);
  }

  @Autowired
  private void addEventManager(ApplicationEventPublisher publisher) {
    projectService.setApplicationEventPublisher(publisher);
  }
}
