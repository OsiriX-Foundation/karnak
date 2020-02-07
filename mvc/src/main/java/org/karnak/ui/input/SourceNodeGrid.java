package org.karnak.ui.input;

import org.karnak.data.input.SourceNode;

import com.vaadin.flow.component.grid.Grid;

/**
 * Grid of source nodes, handling the visual presentation and filtering of a set
 * of items. This version uses an in-memory data source that is suitable for
 * small data sets.
 */
@SuppressWarnings("serial")
public class SourceNodeGrid extends Grid<SourceNode> {
    public SourceNodeGrid() {
        setSizeFull();

        addColumn(SourceNode::getSrcAeTitle).setHeader("Source AETitle").setFlexGrow(20).setSortable(true);

        addColumn(SourceNode::getDstAeTitle).setHeader("Destination AETitle").setFlexGrow(20).setSortable(true);

        addColumn(SourceNode::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }

    public SourceNode getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(SourceNode data) {
        getDataCommunicator().refresh(data);
    }
}
