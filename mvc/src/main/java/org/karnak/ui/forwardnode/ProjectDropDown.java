package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.data.gateway.Project;
import org.karnak.ui.data.ProjectDataProvider;

public class ProjectDropDown extends ComboBox<Project> {
    ProjectDataProvider projectDataProvider;

    public ProjectDropDown() {
        projectDataProvider = new ProjectDataProvider();

        setItems(projectDataProvider.getAllProjects());
        setItemLabelGenerator(Project::getName);
    }
}
