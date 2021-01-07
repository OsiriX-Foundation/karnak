package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.backend.service.ProjectDataProvider;
import org.karnak.data.gateway.Project;

public class ProjectDropDown extends ComboBox<Project> {
    ProjectDataProvider projectDataProvider;

    public ProjectDropDown() {
        projectDataProvider = new ProjectDataProvider();

        setItems(projectDataProvider.getAllProjects());
        setItemLabelGenerator(Project::getName);
    }
}
