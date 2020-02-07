package org.karnak.ui.output;

import org.karnak.data.output.Destination;

import com.vaadin.flow.component.grid.Grid;

/**
 * Grid of destinations, handling the visual presentation and filtering of a set
 * of items. This version uses an in-memory data source that is suitable for
 * small data sets.
 */
@SuppressWarnings("serial")
public class DestinationGrid extends Grid<Destination> {
    public DestinationGrid() {
        setSizeFull();

        addColumn(Destination::getType).setHeader("Type").setFlexGrow(20).setSortable(true);

        addColumn(Destination::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }

    public Destination getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Destination data) {
        getDataCommunicator().refresh(data);
    }
}
