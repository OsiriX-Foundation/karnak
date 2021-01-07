package org.karnak.frontend.project;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.Project;
import org.karnak.backend.service.ProjectDataProvider;

public class GridProject extends Grid<Project> {

    private final ProjectDataProvider projectDataProvider;

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
        if (row != null) {
            getSelectionModel().select(row);
        } else {
            getSelectionModel().deselectAll();
        }
    }
}
