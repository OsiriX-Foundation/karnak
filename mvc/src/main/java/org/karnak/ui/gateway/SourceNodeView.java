package org.karnak.ui.gateway;

import org.karnak.data.NodeEventType;
import org.karnak.data.NodeEvent;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.DicomSourceNode;

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
    private final SourceNodeLogic sourceNodeLogic;

    private TextField filter;
    private Button newSourceNode;
    private SourceNodeGrid grid;
    private GatewayViewLogic gatewayViewLogic;

    private SourceNodeForm sourceNodeForm;
    VerticalLayout barAndGridLayout;

    public SourceNodeView(DataService dataService, GatewayViewLogic gatewayViewLogic) {
        this.gatewayViewLogic = gatewayViewLogic;
        this.dataProvider = new SourceNodeDataProvider(dataService);
        this.sourceNodeLogic = new SourceNodeLogic(gatewayViewLogic, this);
        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new SourceNodeGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> sourceNodeLogic.rowSelected(event.getValue()));

        sourceNodeForm = new SourceNodeForm(sourceNodeLogic);

        barAndGridLayout = new VerticalLayout();
        barAndGridLayout.setPadding(false);
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(sourceNodeForm);

        sourceNodeLogic.init(null);
    }

    public SourceNodeLogic getSourceNodeLogic() {
        return sourceNodeLogic;
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
        newSourceNode.addClickListener(click -> sourceNodeLogic.newSourceNode());

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

    protected void selectRow(DicomSourceNode row) {
        grid.getSelectionModel().select(row);
    }

    protected DicomSourceNode getSelectedRow() {
        return grid.getSelectedRow();
    }

    protected void cancelDestination() {
        showForm(false);
    }

    protected void updateSourceNode(DicomSourceNode data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
        dataProvider.save(data);
        if (data.getForwardNode() != null) {
            sourceNodeLogic.getOutputLogic().getApplicationEventPublisher()
                .publishEvent(new NodeEvent(data, eventType));
        }
        showForm(false);
    }

    protected void removeSourceNode(DicomSourceNode data) {
        if (data.getForwardNode() != null) {
            sourceNodeLogic.getOutputLogic().getApplicationEventPublisher()
            .publishEvent(new NodeEvent(data, NodeEventType.REMOVE));
        }
        dataProvider.delete(data);
        showForm(false);
    }

    protected void editSourceNode(DicomSourceNode data) {
        showForm(data != null);
        sourceNodeForm.editSourceNode(data);
    }

    protected void showForm(boolean show) {
        sourceNodeForm.setVisible(show);
        sourceNodeForm.setEnabled(show);
        barAndGridLayout.setVisible(!show);
        gatewayViewLogic.showForwardNodeForm(!show);
    }

    public boolean hasChanges() {
        return dataProvider.hasChanges();
    }
}
