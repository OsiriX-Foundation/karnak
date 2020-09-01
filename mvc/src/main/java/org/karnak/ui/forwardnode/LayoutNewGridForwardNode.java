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
    private final ForwardNodeDataProvider dataProvider;
    private NewForwardNode newForwardNode;

    private Button buttonNewForwardNode;
    private TextField textFieldNewAETitleForwardNode;
    private Button buttonAddNewForwardNode;
    private Button buttonCancelNewForwardNode;

    public LayoutNewGridForwardNode() {
        dataProvider = new ForwardNodeDataProvider();
        newForwardNode = new NewForwardNode();
        add(newForwardNode);
        buttonNewForwardNode = newForwardNode.getNewForwardNode();
        textFieldNewAETitleForwardNode = newForwardNode.getNewAETitleForwardNode();
        buttonAddNewForwardNode = newForwardNode.getAddNewForwardNode();

        createNewForwardNode();
    }

    private void createNewForwardNode() {
        buttonAddNewForwardNode.addClickListener(click -> {
            // TODO: Select in the grid the new Forward Node
            // textFieldNewAETitleForwardNode.getValue();

            final ForwardNode forwardNode = new ForwardNode(textFieldNewAETitleForwardNode.getValue());
            updateForwardNode(forwardNode);
            // grid.getSelectionModel().select(forwardNode);
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
