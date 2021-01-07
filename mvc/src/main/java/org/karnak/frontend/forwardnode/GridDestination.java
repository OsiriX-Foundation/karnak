package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.gateway.Destination;

public class GridDestination extends Grid<Destination> {
    public GridDestination() {
        setSizeFull();

        addColumn(Destination::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);

        addColumn(Destination::getType).setHeader("Type").setFlexGrow(20).setSortable(true);
    }

    public Destination getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Destination data) {
        getDataCommunicator().refresh(data);
    }
}
