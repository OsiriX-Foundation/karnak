package org.karnak.ui.output;

import org.karnak.data.NodeEventType;
import org.karnak.data.OutputNodeEvent;
import org.karnak.data.output.ForwardNode;
import org.karnak.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

/**
 * A view for performing create-read-update-delete operations on output
 * configuration.
 *
 * See also {@link OutputLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@Route(value = "output", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Output configuration")
@SuppressWarnings("serial")
public class OutputView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Output";

    private final ForwardNodeDataProvider dataProvider;
    private final OutputLogic viewLogic;

    private TextField filter;
    private Button newForwardNode;
    private ForwardNodeGrid grid;

    private ForwardNodeForm form;

    public OutputView() {
        this.dataProvider = buidDataProvider();
        this.viewLogic = new OutputLogic(this);

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
    private void addEventManager(ApplicationEventPublisher publisher){       
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

        newForwardNode = new Button("New forward node");
        newForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newForwardNode.addClickListener(click -> viewLogic.newForwardNode());
        // CTRL+N will create a new window which is unavoidable
        newForwardNode.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newForwardNode);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    protected void showError(String msg) {
        Notification.show(msg);
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
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD: NodeEventType.UPDATE;
        viewLogic.getApplicationEventPublisher().publishEvent(new OutputNodeEvent(data, eventType));
        dataProvider.save(data);
        grid.getDataProvider().refreshAll();
    }

    protected void removeForwardNode(ForwardNode data) {
        viewLogic.getApplicationEventPublisher().publishEvent(new OutputNodeEvent(data, NodeEventType.REMOVE));
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
