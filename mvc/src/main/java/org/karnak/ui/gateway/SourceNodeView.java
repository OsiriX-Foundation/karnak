package org.karnak.ui.gateway;

import org.karnak.data.NodeEventType;
import org.karnak.data.OutputNodeEvent;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.SourceNode;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

/**
 * A view for performing create-read-update-delete operations on sSource node configuration.
 *
 * See also {@link SourceNodeLogic} for fetching the data, the actual CRUD operations and controlling the view based on
 * events from outside.
 */

@SuppressWarnings("serial")
public class SourceNodeView extends HorizontalLayout {
    private final SourceNodeDataProvider dataProvider;
    private final SourceNodeLogic viewLogic;

    private TextField filter;
    private Button newSourceNode;
    private SourceNodeGrid grid;

    private SourceNodeForm form;

    public SourceNodeView(DataService dataService, GatewayViewLogic outputLogic) {
        this.dataProvider = new SourceNodeDataProvider(dataService);
        this.viewLogic = new SourceNodeLogic(outputLogic, this);
        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new SourceNodeGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        form = new SourceNodeForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.setPadding(false);
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init(null);
    }

    public SourceNodeLogic getViewLogic() {
        return viewLogic;
    }

    protected void setForwardNode(ForwardNode forwardNode) {
        dataProvider.setForwardNode(forwardNode);
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter properties of sources");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newSourceNode = new Button("Source");
        newSourceNode.getElement().setAttribute("title", "New source");
        newSourceNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newSourceNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newSourceNode.addClickListener(click -> viewLogic.newSourceNode());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
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

    protected void cancelDestination() {
        showForm(false);
    }

    protected void updateSourceNode(SourceNode data) {
        if (data.getForwardNode() != null) {
            NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
            viewLogic.getOutputLogic().getApplicationEventPublisher()
                .publishEvent(new OutputNodeEvent(data, eventType));
        }
        dataProvider.save(data);
        showForm(false);
    }

    protected void removeSourceNode(SourceNode data) {
        if (data.getForwardNode() != null) {
        viewLogic.getOutputLogic().getApplicationEventPublisher()
            .publishEvent(new OutputNodeEvent(data, NodeEventType.REMOVE));
        }
        dataProvider.delete(data);
        showForm(false);
    }

    protected void editSourceNode(SourceNode data) {
        showForm(data != null);
        form.editSourceNode(data);
    }

    protected void showForm(boolean show) {
        form.setVisible(show);
        form.setEnabled(show);
    }

    public boolean hasChanges() {
        return dataProvider.hasChanges();
    }
}
