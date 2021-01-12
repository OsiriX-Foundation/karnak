package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectDropDown extends ComboBox<ProjectEntity> {

  private final ProjectService projectService;

  @Autowired
  public ProjectDropDown(final ProjectService projectService) {
    this.projectService = projectService;

    setItems(projectService.getAllProjects());
    setItemLabelGenerator(ProjectEntity::getName);
  }
}
