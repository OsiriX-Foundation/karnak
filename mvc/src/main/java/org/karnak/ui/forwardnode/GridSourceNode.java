package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.SourceNode;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DicomSourceNode;

public class GridSourceNode extends Grid<DicomSourceNode> {
    public GridSourceNode() {
        setSizeFull();

        addColumn(DicomSourceNode::getAeTitle).setHeader("AET title").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getHostname).setHeader("Hostname").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }
}
