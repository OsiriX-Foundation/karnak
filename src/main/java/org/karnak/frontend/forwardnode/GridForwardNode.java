package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.backend.data.entity.ForwardNodeEntity;

public class GridForwardNode extends Grid<ForwardNodeEntity> {

  public GridForwardNode() {
    setSizeFull();

    addColumn(ForwardNodeEntity::getFwdAeTitle)
        .setHeader("Forward AETitle")
        .setFlexGrow(20)
        .setSortable(true);

    addColumn(ForwardNodeEntity::getDescription)
        .setHeader("Description")
        .setFlexGrow(20)
        .setSortable(true);
  }

  public ForwardNodeEntity getSelectedRow() {
    return asSingleSelect().getValue();
  }

  public void refresh(ForwardNodeEntity data) {
    getDataCommunicator().refresh(data);
  }

  public void selectRow(ForwardNodeEntity row) {
    getSelectionModel().select(row);
  }
}
