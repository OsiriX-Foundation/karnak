package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.ForwardNodeAPI;
import org.karnak.backend.service.ForwardNodeDataProvider;
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
  private final ForwardNodeAPI forwardNodeAPI;
  private final LayoutNewGridForwardNode layoutNewGridForwardNode;
  private final LayoutEditForwardNode layoutEditForwardNode;
  private final ForwardNodeViewLogic forwardNodeViewLogic;

  public ForwardNodeView() {
    setSizeFull();
    ForwardNodeDataProvider dataProvider = new ForwardNodeDataProvider();
    forwardNodeAPI = new ForwardNodeAPI(dataProvider);
    forwardNodeViewLogic = new ForwardNodeViewLogic(forwardNodeAPI);
    layoutNewGridForwardNode = new LayoutNewGridForwardNode(forwardNodeViewLogic, forwardNodeAPI);
    layoutEditForwardNode = new LayoutEditForwardNode(forwardNodeViewLogic, forwardNodeAPI);

    layoutNewGridForwardNode.setWidth("30%");
    layoutEditForwardNode.setWidth("70%");
    add(layoutNewGridForwardNode, layoutEditForwardNode);
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    Long idForwardNode = forwardNodeViewLogic.enter(parameter);
    ForwardNodeEntity currentForwardNodeEntity = null;
    if (idForwardNode != null) {
      currentForwardNodeEntity = forwardNodeAPI.getForwardNodeById(idForwardNode);
    }
    layoutNewGridForwardNode.load(currentForwardNodeEntity);
    layoutEditForwardNode.load(currentForwardNodeEntity);
  }

  @Autowired
  private void addEventManager(ApplicationEventPublisher publisher) {
    forwardNodeAPI.setApplicationEventPublisher(publisher);
  }
}
