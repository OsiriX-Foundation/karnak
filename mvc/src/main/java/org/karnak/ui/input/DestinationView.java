package org.karnak.ui.input;

import org.karnak.data.InputNodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.input.Destination;
import org.karnak.data.input.SourceNode;

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
 * A view for performing create-read-update-delete operations on destination configuration.
 *
 * See also {@link DestinationLogic} for fetching the data, the actual CRUD operations and controlling the view based on
 * events from outside.
 */
@SuppressWarnings("serial")
public class DestinationView extends HorizontalLayout {
    private final DestinationDataProvider dataProvider;
    private final DestinationLogic viewLogic;

    private TextField filter;
    private Button newDestination;
    private DestinationGrid grid;

    private DestinationForm form;

    public DestinationView(DataService dataService, InputLogic inputLogic) {
        this.dataProvider = new DestinationDataProvider(dataService);
        this.viewLogic = new DestinationLogic(inputLogic, this);

        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new DestinationGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        form = new DestinationForm(viewLogic);

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

    public DestinationLogic getViewLogic() {
        return viewLogic;
    }

    protected void setSourceNode(SourceNode sourceNode) {
        dataProvider.setSourceNode(sourceNode);
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter properties of destination");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newDestination = new Button("New destination");
        newDestination.getElement().setAttribute("title", "New destination");
        newDestination.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestination.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newDestination.addClickListener(click -> viewLogic.newDestination());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        topLayout.add(filter);
        topLayout.add(newDestination);
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

    protected void setNewDestinationEnabled(boolean enabled) {
        newDestination.setEnabled(enabled);
    }

    protected void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    protected void selectRow(Destination row) {
        grid.getSelectionModel().select(row);
    }

    protected Destination getSelectedRow() {
        return grid.getSelectedRow();
    }

    protected void cancelDestination() {
        showForm(false);
    }

    protected void updateDestination(Destination data) {
        if (data.getSourceNode() != null) {
            NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
            viewLogic.getInputLogic().getApplicationEventPublisher()
                .publishEvent(new InputNodeEvent(data, eventType));
        }

        dataProvider.save(data);
        showForm(false);
    }

    protected void removeDestination(Destination data) {
        if (data.getSourceNode() != null) {
            viewLogic.getInputLogic().getApplicationEventPublisher()
                .publishEvent(new InputNodeEvent(data, NodeEventType.REMOVE));
        }
        dataProvider.delete(data);
        showForm(false);
   }

    protected void editDestination(Destination data) {
        showForm(data != null);
        form.editDestination(data);
    }

    protected void showForm(boolean show) {
        form.setVisible(show);
        form.setEnabled(show);
    }

    public boolean hasChanges() {
        return dataProvider.hasChanges();
    }
}
