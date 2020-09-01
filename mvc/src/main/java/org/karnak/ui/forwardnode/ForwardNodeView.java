package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.MainLayout;

@Route(value = "forwardnode", layout = MainLayout.class)
@PageTitle("KARNAK - Forward node")
public class ForwardNodeView extends HorizontalLayout {
    public static final String VIEW_NAME = "Gateway";
    private LayoutNewGridForwardNode layoutNewGridForwardNode = new LayoutNewGridForwardNode();

    public ForwardNodeView() {
        setSizeFull();
        add(layoutNewGridForwardNode);
    }
}
