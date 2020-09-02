package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.api.ForwardNodeAPI;

public class LayoutNewGridForwardNode extends VerticalLayout {
    private final ForwardNodeViewLogic forwardNodeViewLogic;
    private final ForwardNodeAPI forwardNodeAPI;

    private NewForwardNode newForwardNode;
    private GridForwardNode gridForwardNode;

    private Button buttonNewForwardNode;
    private TextField textFieldNewAETitleForwardNode;
    private Button buttonAddNewForwardNode;
    private Button buttonCancelNewForwardNode;

    public LayoutNewGridForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPI forwardNodeAPI) {
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

    public void load(ForwardNode forwardNode) {
        if (forwardNode != null && forwardNode != gridForwardNode.getSelectedRow()) {
            gridForwardNode.selectRow(forwardNode);
        } else {
            gridForwardNode.getSelectionModel().deselectAll();
        }
    }

    private void eventNewForwardNode() {
        buttonAddNewForwardNode.addClickListener(click -> {
            final ForwardNode forwardNode = new ForwardNode(textFieldNewAETitleForwardNode.getValue());
            forwardNodeAPI.addForwardNode(forwardNode);
            gridForwardNode.getSelectionModel().select(forwardNode);
            forwardNodeViewLogic.editForwardNode(forwardNode);
        });
    }

    private void eventGridSelection() {
        gridForwardNode.asSingleSelect().addValueChangeListener(event -> {
            forwardNodeViewLogic.editForwardNode(event.getValue());
        });
    }
}
