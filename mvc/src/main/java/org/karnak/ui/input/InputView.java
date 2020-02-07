package org.karnak.ui.input;

import org.karnak.data.InputNodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.input.SourceNode;
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

/**
 * A view for performing create-read-update-delete operations on input
 * configuration.
 *
 * See also {@link InputLogic} for fetching the data, the actual CRUD operations
 * and controlling the view based on events from outside.
 */
@Route(value = "input", layout = MainLayout.class)
@PageTitle("Input configuration")
@SuppressWarnings("serial")
public class InputView extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Input";

    private final SourceNodeDataProvider dataProvider;
    private final InputLogic viewLogic;

    private TextField filter;
    private Button newSourceNode;
    private SourceNodeGrid grid;

    private SourceNodeForm form;

    public InputView() {
        this.dataProvider = buidDataProvider();
        this.viewLogic = new InputLogic(this);

        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new SourceNodeGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        form = new SourceNodeForm(this.dataProvider.getDataService(), viewLogic);

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

    private SourceNodeDataProvider buidDataProvider() {
        return new SourceNodeDataProvider();
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter properties of source node");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newSourceNode = new Button("New source node");
        newSourceNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newSourceNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newSourceNode.addClickListener(click -> viewLogic.newSourceNode());
        // CTRL+N will create a new window which is unavoidable
        newSourceNode.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newSourceNode);
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

    protected void setNewSourceNodeEnabled(boolean enabled) {
        newSourceNode.setEnabled(enabled);
    }

    protected void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    protected void selectRow(SourceNode row) {
        grid.getSelectionModel().select(row);
    }

    protected SourceNode getSelectedRow() {
        return grid.getSelectedRow();
    }

    protected SourceNode getSourceNodeById(Long dataId) {
        return dataProvider.get(dataId);
    }

    protected void updateSourceNode(SourceNode data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD: NodeEventType.UPDATE;
        viewLogic.getApplicationEventPublisher().publishEvent(new InputNodeEvent(data, eventType));
        dataProvider.save(data);
        grid.getDataProvider().refreshAll();
    }

    protected void removeSourceNode(SourceNode data) {
        viewLogic.getApplicationEventPublisher().publishEvent(new InputNodeEvent(data, NodeEventType.REMOVE));
        dataProvider.delete(data);
    }

    protected void editSourceNode(SourceNode data) {
        showForm(data != null);
        form.editSourceNode(data);
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
