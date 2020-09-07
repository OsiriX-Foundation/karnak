package org.karnak.ui.forwardnode;

public class DestinationViewLogic {
    LayoutEditForwardNode currentLayout;

    public DestinationViewLogic(LayoutEditForwardNode currentLayout) {
        this.currentLayout = currentLayout;
    }

    public void updateForwardNodeInEditView() {
        this.currentLayout.load(currentLayout.currentForwardNode);
    }
}
