package org.karnak.ui.research;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.data.gateway.Research;

import java.util.ArrayList;

public class GridResearch extends Grid<Research> {
    ListDataProvider<Research> listDataProvider;

    public GridResearch() {
        setWidthFull();
        setHeightByRows(true);
        setItems(new ArrayList<>());

        listDataProvider = (ListDataProvider<Research>) getDataProvider();
        addColumn(Research::getName).setHeader("Project Name").setFlexGrow(15)
                .setSortable(true);
        addColumn(Research::getSecret).setHeader("Project Secret").setFlexGrow(15)
                .setSortable(true);
    }
}
