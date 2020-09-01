package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.MainLayout;
import org.karnak.ui.gateway.ForwardNodeDataProvider;

@Route(value = "forwardnode", layout = MainLayout.class)
@RouteAlias(value = "forwardnode", layout = MainLayout.class)
@PageTitle("KARNAK - Forward node")
public class ForwardNodeView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Gateway";
    private LayoutNewGridForwardNode layoutNewGridForwardNode;
    private LayoutEditForwardNode layoutEditForwardNode;
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeDataProvider dataProvider;

    public ForwardNodeView() {
        setSizeFull();
        dataProvider = new ForwardNodeDataProvider();
        forwardNodeViewLogic = new ForwardNodeViewLogic(this);
        layoutNewGridForwardNode = new LayoutNewGridForwardNode(forwardNodeViewLogic, dataProvider);
        layoutEditForwardNode = new LayoutEditForwardNode(forwardNodeViewLogic, dataProvider);
        add(layoutNewGridForwardNode, layoutEditForwardNode);
    }

    protected ForwardNode getForwardNodeById(Long dataId) {
        return dataProvider.get(dataId);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Long idForwardNode = forwardNodeViewLogic.enter(parameter);
        if (idForwardNode != null) {
            layoutEditForwardNode.load(idForwardNode);
        }
    }
}
