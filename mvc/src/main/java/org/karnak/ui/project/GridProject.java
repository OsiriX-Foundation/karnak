package org.karnak.ui.project;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.gateway.Project;
import org.karnak.ui.data.ProjectDataProvider;

public class GridProject extends Grid<Project> {
    private ProjectDataProvider projectDataProvider;

    public GridProject(ProjectDataProvider projectDataProvider) {
        this.projectDataProvider = projectDataProvider;
        setDataProvider(this.projectDataProvider);
        setWidthFull();
        setHeightByRows(true);

        addColumn(Project::getName).setHeader("Project Name").setFlexGrow(15)
                .setSortable(true);
        addColumn(project -> project.getProfile().getName()).setHeader("Desidenfication profile").setFlexGrow(15)
                .setSortable(true);
    }

    public void selectRow(Project row) {
        getSelectionModel().select(row);
    }
}
