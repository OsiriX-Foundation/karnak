package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.ForwardNodeAPIService;
import org.karnak.frontend.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;

@Route(value = "forwardnode", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("KARNAK - Forward node")
@Secured({"ADMIN"})
public class ForwardNodeView extends HorizontalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Gateway";
    private final ForwardNodeAPIService forwardNodeAPIService;
    private final LayoutNewGridForwardNode layoutNewGridForwardNode;
    private final LayoutEditForwardNode layoutEditForwardNode;
    private final ForwardNodeViewLogic forwardNodeViewLogic;

    @Autowired
    public ForwardNodeView(LayoutEditForwardNode layoutEditForwardNode,
        final ForwardNodeAPIService forwardNodeAPIService) {
        setSizeFull();
        this.forwardNodeAPIService = forwardNodeAPIService;
        this.forwardNodeViewLogic = new ForwardNodeViewLogic(forwardNodeAPIService);
        this.layoutNewGridForwardNode = new LayoutNewGridForwardNode(forwardNodeViewLogic,
            forwardNodeAPIService);

        this.layoutEditForwardNode = layoutEditForwardNode;
    }

    @PostConstruct
    public void init() {
        layoutNewGridForwardNode.setWidth("30%");
        this.layoutEditForwardNode.setWidth("70%");
        add(layoutNewGridForwardNode, this.layoutEditForwardNode);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Long idForwardNode = forwardNodeViewLogic.enter(parameter);
        ForwardNodeEntity currentForwardNodeEntity = null;
        if (idForwardNode != null) {
            currentForwardNodeEntity = forwardNodeAPIService.getForwardNodeById(idForwardNode);
        }
        layoutNewGridForwardNode.load(currentForwardNodeEntity);
        layoutEditForwardNode.load(currentForwardNodeEntity);
    }

    @Autowired
    private void addEventManager(ApplicationEventPublisher publisher) {
        forwardNodeAPIService.setApplicationEventPublisher(publisher);
    }
}
