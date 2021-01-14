package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;

public class GridSourceNode extends Grid<DicomSourceNodeEntity> {

  public GridSourceNode() {
    setSizeFull();

    addColumn(DicomSourceNodeEntity::getAeTitle).setHeader("AET title").setFlexGrow(20)
        .setSortable(true);

    addColumn(DicomSourceNodeEntity::getHostname).setHeader("Hostname").setFlexGrow(20)
        .setSortable(true);

    addColumn(DicomSourceNodeEntity::getDescription).setHeader("Description").setFlexGrow(20)
        .setSortable(true);
  }
}
