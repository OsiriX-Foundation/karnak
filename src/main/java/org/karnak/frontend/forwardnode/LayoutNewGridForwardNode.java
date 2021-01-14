package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.ForwardNodeAPI;

public class LayoutNewGridForwardNode extends VerticalLayout {

  private final ForwardNodeViewLogic forwardNodeViewLogic;
  private final ForwardNodeAPI forwardNodeAPI;

  private final NewForwardNode newForwardNode;
  private final GridForwardNode gridForwardNode;

  private final Button buttonNewForwardNode;
  private final TextField textFieldNewAETitleForwardNode;
  private final Button buttonAddNewForwardNode;
  private Button buttonCancelNewForwardNode;

  public LayoutNewGridForwardNode(
      ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPI forwardNodeAPI) {
    this.forwardNodeViewLogic = forwardNodeViewLogic;
    this.forwardNodeAPI = forwardNodeAPI;
    newForwardNode = new NewForwardNode();
    gridForwardNode = new GridForwardNode();
    gridForwardNode.setDataProvider(forwardNodeAPI.getDataProvider());
    add(newForwardNode, gridForwardNode);

    buttonNewForwardNode = newForwardNode.getNewForwardNode();
    textFieldNewAETitleForwardNode = newForwardNode.getNewAETitleForwardNode();
    buttonAddNewForwardNode = newForwardNode.getAddNewForwardNode();

    eventNewForwardNode();
    eventGridSelection();
  }

  public void load(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null && forwardNodeEntity != gridForwardNode.getSelectedRow()) {
      gridForwardNode.selectRow(forwardNodeEntity);
    } else {
      gridForwardNode.getSelectionModel().deselectAll();
    }
  }

  private void eventNewForwardNode() {
    buttonAddNewForwardNode.addClickListener(
        click -> {
          eventAddForwardNode(new ForwardNodeEntity(textFieldNewAETitleForwardNode.getValue()));
        });
    textFieldNewAETitleForwardNode.addKeyDownListener(
        Key.ENTER,
        keyDownEvent -> {
          eventAddForwardNode(new ForwardNodeEntity(textFieldNewAETitleForwardNode.getValue()));
        });
  }

  private void eventAddForwardNode(ForwardNodeEntity forwardNodeEntity) {
    forwardNodeAPI.addForwardNode(forwardNodeEntity);
    gridForwardNode.getSelectionModel().select(forwardNodeEntity);
    forwardNodeViewLogic.editForwardNode(forwardNodeEntity);
  }

  private void eventGridSelection() {
    gridForwardNode
        .asSingleSelect()
        .addValueChangeListener(
            event -> {
              forwardNodeViewLogic.editForwardNode(event.getValue());
            });
  }
}
