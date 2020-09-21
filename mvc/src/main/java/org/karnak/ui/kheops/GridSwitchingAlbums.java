package org.karnak.ui.kheops;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.List;

public class GridSwitchingAlbums extends Grid<KheopsAlbums> {
    public GridSwitchingAlbums() {
        setWidthFull();
        setMinHeight("20");

        addColumn(KheopsAlbums::getUrlAPI).setHeader("URL API").setFlexGrow(20).setSortable(true);

        addColumn(KheopsAlbums::getAuthorizationDestination).setHeader("Token destination").setSortable(true);

        addColumn(KheopsAlbums::getAuthorizationSource).setHeader("Token source").setSortable(true);

        addColumn(KheopsAlbums::getCondition).setHeader("Condition").setSortable(true);
    }

    public KheopsAlbums getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(KheopsAlbums data) {
        getDataCommunicator().refresh(data);
    }

    public void initialize(List<KheopsAlbums> data) {
        if (data != null) {
            setItems(data);
        }
    }
}
