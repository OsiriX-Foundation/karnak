package org.karnak.ui.gateway;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;

/**
 * A view for performing create-read-update-delete operations on output configuration.
 *
 * See also {@link GatewayViewLogic} for fetching the data, the actual CRUD operations and controlling the view based on
 * events from outside.
 */
@Route(value = "gateway", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("KARNAK - Gateway")
@SuppressWarnings("serial")
public class GatewayView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Gateway";

    private final ForwardNodeDataProvider dataProvider;
    private final GatewayViewLogic viewLogic;

    private TextField filter;
    private Button newForwardNode;

    private TextField newAETitleForwardNode;
    private Button addNewForwardNode;
    private Button cancelNewForwardNode;
    private ForwardNodeGrid grid;

    private ForwardNodeForm form;

    public GatewayView() {
        this.dataProvider = buidDataProvider();
        this.viewLogic = new GatewayViewLogic(this);

        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new ForwardNodeGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        form = new ForwardNodeForm(this.dataProvider.getDataService(), viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    @Autowired
    private void addEventManager(ApplicationEventPublisher publisher) {
        viewLogic.setApplicationEventPublisher(publisher);
    }

    private ForwardNodeDataProvider buidDataProvider() {
        return new ForwardNodeDataProvider();
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter properties of forward node");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);


        newAETitleForwardNode = new TextField();
        newAETitleForwardNode.setPlaceholder("Forward AETitle");
        newAETitleForwardNode.setVisible(false);

        addNewForwardNode = new Button("Add");
        addNewForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addNewForwardNode.setVisible(false);
        addNewForwardNode.addClickListener(click -> {
            form.setEnabled(false);
            filter.setVisible(true);
            newForwardNode.setVisible(true);
            newAETitleForwardNode.setVisible(false);
            addNewForwardNode.setVisible(false);
            cancelNewForwardNode.setVisible(false);
            final ForwardNode forwardNode = new ForwardNode(newAETitleForwardNode.getValue());
            updateForwardNode(forwardNode);
            grid.getSelectionModel().select(forwardNode);
        });

        cancelNewForwardNode= new Button("Cancel");
        cancelNewForwardNode.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelNewForwardNode.setVisible(false);
        cancelNewForwardNode.addClickListener(click -> {
            form.setEnabled(false);
            filter.setVisible(true);
            newForwardNode.setVisible(true);
            newAETitleForwardNode.setVisible(false);
            addNewForwardNode.setVisible(false);
            cancelNewForwardNode.setVisible(false);
        });

        newForwardNode = new Button("New forward node");
        newForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newForwardNode.addClickListener(click -> {
            form.setEnabled(false);
            filter.setVisible(false);
            newForwardNode.setVisible(false);
            newAETitleForwardNode.setVisible(true);
            newAETitleForwardNode.setValue("");
            addNewForwardNode.setVisible(true);
            cancelNewForwardNode.setVisible(true);

        });
        // CTRL+N will create a new window which is unavoidable
        newForwardNode.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        //topLayout.add(filter);
        topLayout.add(newForwardNode);
        topLayout.add(newAETitleForwardNode, addNewForwardNode, cancelNewForwardNode);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    protected void showError(String msg) {
        Div content = new Div();
        content.addClassName("my-style");
        content.setText(msg);

        Notification notification = new Notification(content);
        notification.setDuration(5000);

        // @formatter:off
        String styles = ".my-style { " + "  color: red;" + " }";
        // @formatter:on

        /*
         * The code below register the style file dynamically. Normally you use @StyleSheet annotation for the component
         * class. This way is chosen just to show the style file source code.
         */
        StreamRegistration resource =
            UI.getCurrent().getSession().getResourceRegistry().registerResource(new StreamResource("styles.css", () -> {
                byte[] bytes = styles.getBytes(StandardCharsets.UTF_8);
                return new ByteArrayInputStream(bytes);
            }));
        UI.getCurrent().getPage().addStyleSheet("base://" + resource.getResourceUri().toString());

        notification.open();
    }

    protected void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    protected void setNewForwardNodeEnabled(boolean enabled) {
        newForwardNode.setEnabled(enabled);
    }

    protected void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    protected void selectRow(ForwardNode row) {
        grid.getSelectionModel().select(row);
    }

    protected ForwardNode getSelectedRow() {
        return grid.getSelectedRow();
    }

    protected ForwardNode getForwardNodeById(Long dataId) {
        return dataProvider.get(dataId);
    }

    protected void updateForwardNode(ForwardNode data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
        if (eventType == NodeEventType.ADD) {
            Optional<ForwardNode> val = dataProvider.getDataService().getAllForwardNodes().stream()
                .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
            if (val.isPresent()) {
                grid.getDataProvider().refreshAll();
                showError("Cannot add this new node because the AE-Title already exists!");
                return;
            }
        }
        dataProvider.save(data);
        grid.getDataProvider().refreshAll();
        viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(data, eventType));
    }

    protected void removeForwardNode(ForwardNode data) {
        viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(data, NodeEventType.REMOVE));
        dataProvider.delete(data);
    }

    protected void editForwardNode(ForwardNode data) {
        showForm(data != null);
        form.editForwardNode(data);
    }

    protected void showForm(boolean show) {
        form.setVisible(true);
        form.setEnabled(show);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }

    public void validateView() {
        form.validateView();
    }
}
