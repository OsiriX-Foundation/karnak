package org.karnak.ui.project;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.data.gateway.Project;

import java.util.ArrayList;

public class GridProject extends Grid<Project> {
    ListDataProvider<Project> listDataProvider;

    public GridProject() {
        setWidthFull();
        setHeightByRows(true);
        setItems(new ArrayList<>());

        listDataProvider = (ListDataProvider<Project>) getDataProvider();
        addColumn(Project::getName).setHeader("Project Name").setFlexGrow(15)
                .setSortable(true);
        addColumn(Project::getSecret).setHeader("Project Secret").setFlexGrow(15)
                .setSortable(true);
    }
}
