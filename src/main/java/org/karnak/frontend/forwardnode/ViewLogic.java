package org.karnak.frontend.forwardnode;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.context.ApplicationEventPublisher;

@UIScope
public class ViewLogic {

    private final LayoutEditForwardNode currentLayout;
    private ApplicationEventPublisher applicationEventPublisher;

    public ViewLogic(LayoutEditForwardNode currentLayout) {
        this.currentLayout = currentLayout;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void updateForwardNodeInEditView() {
        this.currentLayout.load(currentLayout.currentForwardNodeEntity);
    }
}
