package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectDataProvider;

public class ProjectDropDown extends ComboBox<ProjectEntity> {

  ProjectDataProvider projectDataProvider;

  public ProjectDropDown() {
    projectDataProvider = new ProjectDataProvider();

    setItems(projectDataProvider.getAllProjects());
    setItemLabelGenerator(ProjectEntity::getName);
  }
}
