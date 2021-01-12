package org.karnak.frontend.project;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectService;

public class GridProject extends Grid<ProjectEntity> {

    private final ProjectService projectService;

    public GridProject(ProjectService projectService) {
        this.projectService = projectService;
        setDataProvider(this.projectService);
        setWidthFull();
        setHeightByRows(true);

        addColumn(ProjectEntity::getName).setHeader("Project Name").setFlexGrow(15)
            .setSortable(true);
        addColumn(project -> project.getProfileEntity().getName())
            .setHeader("Desidenfication profile").setFlexGrow(15)
            .setSortable(true);
    }

    public void selectRow(ProjectEntity row) {
        if (row != null) {
            getSelectionModel().select(row);
        } else {
            getSelectionModel().deselectAll();
        }
    }
}
