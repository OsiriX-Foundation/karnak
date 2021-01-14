package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.DestinationEntity;

public class GridDestination extends Grid<DestinationEntity> {

  public GridDestination() {
    setSizeFull();

    addColumn(DestinationEntity::getDescription).setHeader("Description").setFlexGrow(20)
        .setSortable(true);

    addColumn(DestinationEntity::getType).setHeader("Type").setFlexGrow(20).setSortable(true);
  }

  public DestinationEntity getSelectedRow() {
    return asSingleSelect().getValue();
  }

  public void refresh(DestinationEntity data) {
    getDataCommunicator().refresh(data);
  }
}
