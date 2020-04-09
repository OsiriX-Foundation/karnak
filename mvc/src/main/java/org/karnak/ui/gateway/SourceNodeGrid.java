package org.karnak.ui.gateway;

import org.karnak.data.gateway.DicomSourceNode;

import com.vaadin.flow.component.grid.Grid;

/**
 * Grid of source nodes, handling the visual presentation and filtering of a set
 * of items. This version uses an in-memory data source that is suitable for
 * small data sets.
 */
@SuppressWarnings("serial")
public class SourceNodeGrid extends Grid<DicomSourceNode> {
    public SourceNodeGrid() {
        setSizeFull();

        addColumn(DicomSourceNode::getAeTitle).setHeader("AETitle").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getHostname).setHeader("Hostname").setFlexGrow(20).setSortable(true);

        addColumn(DicomSourceNode::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);
    }

    public DicomSourceNode getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(DicomSourceNode data) {
        getDataCommunicator().refresh(data);
    }
}
