package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.gateway.ForwardNode;

public class GridForwardNode extends Grid<ForwardNode> {

    public GridForwardNode() {
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

    public void selectRow(ForwardNode row) {
        getSelectionModel().select(row);
    }

}
