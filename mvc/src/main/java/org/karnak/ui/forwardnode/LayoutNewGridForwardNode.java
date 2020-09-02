package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.ForwardNodeDataProvider;

import java.util.Optional;

public class LayoutNewGridForwardNode extends VerticalLayout {
    private final ForwardNodeViewLogic forwardNodeViewLogic;
    private final ForwardNodeDataProvider dataProvider;

    private NewForwardNode newForwardNode;
    private GridForwardNode gridForwardNode;

    private Button buttonNewForwardNode;
    private TextField textFieldNewAETitleForwardNode;
    private Button buttonAddNewForwardNode;
    private Button buttonCancelNewForwardNode;

    public LayoutNewGridForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeDataProvider dataProvider) {
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        this.dataProvider = dataProvider;
        newForwardNode = new NewForwardNode();
        gridForwardNode = new GridForwardNode();
        gridForwardNode.setDataProvider(this.dataProvider);
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
            addForwardNode(forwardNode);
            gridForwardNode.getSelectionModel().select(forwardNode);
            forwardNodeViewLogic.editForwardNode(forwardNode);
        });
    }

    private void eventGridSelection() {
        gridForwardNode.asSingleSelect().addValueChangeListener(event -> {
            forwardNodeViewLogic.editForwardNode(event.getValue());
        });
    }

    protected void addForwardNode(ForwardNode data) {
        Optional<ForwardNode> val = dataProvider.getDataService().getAllForwardNodes().stream()
                .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
        if (val.isPresent()) {
            // showError("Cannot add this new node because the AE-Title already exists!");
            return;
        }
        dataProvider.save(data);
    }
}
