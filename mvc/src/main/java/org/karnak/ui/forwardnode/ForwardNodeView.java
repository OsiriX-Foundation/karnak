package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.MainLayout;
import org.karnak.ui.api.ForwardNodeAPI;
import org.karnak.ui.gateway.ForwardNodeDataProvider;

@Route(value = "forwardnode", layout = MainLayout.class)
@RouteAlias(value = "forwardnode", layout = MainLayout.class)
@PageTitle("KARNAK - Forward node")
public class ForwardNodeView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Gateway";
    private ForwardNodeAPI forwardNodeAPI;
    private LayoutNewGridForwardNode layoutNewGridForwardNode;
    private LayoutEditForwardNode layoutEditForwardNode;
    private ForwardNodeViewLogic forwardNodeViewLogic;

    public ForwardNodeView() {
        setSizeFull();
        ForwardNodeDataProvider dataProvider = new ForwardNodeDataProvider();
        forwardNodeAPI = new ForwardNodeAPI(dataProvider);
        forwardNodeViewLogic = new ForwardNodeViewLogic(forwardNodeAPI);
        layoutNewGridForwardNode = new LayoutNewGridForwardNode(forwardNodeViewLogic, forwardNodeAPI);
        layoutEditForwardNode = new LayoutEditForwardNode(forwardNodeViewLogic, forwardNodeAPI);

        layoutNewGridForwardNode.setWidth("25%");
        layoutEditForwardNode.setWidth("75%");
        add(layoutNewGridForwardNode, layoutEditForwardNode);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Long idForwardNode = forwardNodeViewLogic.enter(parameter);
        ForwardNode currentForwardNode = null;
        if (idForwardNode != null) {
            currentForwardNode = forwardNodeAPI.getForwardNodeById(idForwardNode);
        }
        layoutNewGridForwardNode.load(currentForwardNode);
        layoutEditForwardNode.load(currentForwardNode);
    }
}
