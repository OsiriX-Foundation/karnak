package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.DicomSourceNode;

public class GridSourceNode extends Grid<DicomSourceNode> {
    public GridSourceNode() {
        setSizeFull();

        addColumn(DicomSourceNode::getAeTitle).setHeader("AET title").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getHostname).setHeader("Hostname").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }
}
