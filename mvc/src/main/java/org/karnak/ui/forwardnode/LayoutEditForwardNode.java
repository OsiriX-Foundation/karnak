package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.ForwardNodeDataProvider;

public class LayoutEditForwardNode extends VerticalLayout {
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeDataProvider dataProvider;
    private EditAETitleDescription editAETitleDescription;

    public LayoutEditForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeDataProvider dataProvider) {
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        this.dataProvider = dataProvider;

        editAETitleDescription = new EditAETitleDescription();
        add(editAETitleDescription);
    }

    public void load(long idForwardNode) {
        ForwardNode forwardNode = forwardNodeViewLogic.findForwardNode(idForwardNode);
        if (forwardNode != null) {
            editAETitleDescription.setForwardNode(forwardNode);
        }
    }
}
