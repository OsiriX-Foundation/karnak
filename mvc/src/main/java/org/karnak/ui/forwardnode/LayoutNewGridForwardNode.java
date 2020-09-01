package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
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

    public LayoutNewGridForwardNode(ForwardNodeViewLogic forwardNodeViewLogic) {
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        dataProvider = new ForwardNodeDataProvider();
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

    private void eventNewForwardNode() {
        buttonAddNewForwardNode.addClickListener(click -> {
            final ForwardNode forwardNode = new ForwardNode(textFieldNewAETitleForwardNode.getValue());
            updateForwardNode(forwardNode);
            gridForwardNode.getSelectionModel().select(forwardNode);
            forwardNodeViewLogic.editForwardNode(forwardNode);
        });
    }

    private void eventGridSelection() {
        gridForwardNode.asSingleSelect().addValueChangeListener(event -> {
            forwardNodeViewLogic.editForwardNode(event.getValue());
        });
    }

    protected void updateForwardNode(ForwardNode data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
        if (eventType == NodeEventType.ADD) {
            Optional<ForwardNode> val = dataProvider.getDataService().getAllForwardNodes().stream()
                    .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
            if (val.isPresent()) {
                // showError("Cannot add this new node because the AE-Title already exists!");
                return;
            }
        }
        dataProvider.save(data);
    }
}
