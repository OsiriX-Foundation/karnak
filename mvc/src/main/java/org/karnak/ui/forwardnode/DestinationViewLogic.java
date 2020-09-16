package org.karnak.ui.forwardnode;

import org.springframework.context.ApplicationEventPublisher;

public class DestinationViewLogic {
    LayoutEditForwardNode currentLayout;
    private ApplicationEventPublisher applicationEventPublisher;

    public DestinationViewLogic(LayoutEditForwardNode currentLayout) {
        this.currentLayout = currentLayout;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void updateForwardNodeInEditView() {
        this.currentLayout.load(currentLayout.currentForwardNode);
    }
}
