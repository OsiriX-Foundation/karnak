package org.karnak.ui.output;

import org.karnak.data.output.ForwardNode;

import com.vaadin.flow.component.grid.Grid;

/**
 * Grid of forward nodes, handling the visual presentation and filtering of a
 * set of items. This version uses an in-memory data source that is suitable for
 * small data sets.
 */
@SuppressWarnings("serial")
public class ForwardNodeGrid extends Grid<ForwardNode> {
    public ForwardNodeGrid() {
        setSizeFull();

        addColumn(ForwardNode::getFwdAeTitle).setHeader("Forward AETitle").setFlexGrow(20).setSortable(true);

        addColumn(ForwardNode::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }

    public ForwardNode getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(ForwardNode data) {
        getDataCommunicator().refresh(data);
    }
}
