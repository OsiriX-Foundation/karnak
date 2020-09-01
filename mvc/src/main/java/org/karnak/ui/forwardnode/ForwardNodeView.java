package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.karnak.ui.MainLayout;

@Route(value = "forwardnode", layout = MainLayout.class)
@RouteAlias(value = "forwardnode", layout = MainLayout.class)
@PageTitle("KARNAK - Forward node")
public class ForwardNodeView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Gateway";
    private LayoutNewGridForwardNode layoutNewGridForwardNode;
    private ForwardNodeViewLogic forwardNodeViewLogic;

    public ForwardNodeView() {
        setSizeFull();

        forwardNodeViewLogic = new ForwardNodeViewLogic(this);
        layoutNewGridForwardNode = new LayoutNewGridForwardNode(forwardNodeViewLogic);
        add(layoutNewGridForwardNode);

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        forwardNodeViewLogic.enter(parameter);
    }

}
