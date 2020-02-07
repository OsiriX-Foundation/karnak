package org.karnak.ui.output;

import org.karnak.data.NodeEventType;
import org.karnak.data.OutputNodeEvent;
import org.karnak.data.output.Destination;
import org.karnak.data.output.DestinationType;
import org.karnak.data.output.ForwardNode;

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
    private Button newDestinationDicom;
    private Button newDestinationStow;
    private DestinationGrid grid;

    private DestinationDicomForm dicomForm;
    private DestinationStowForm stowForm;

    public DestinationView(DataService dataService, OutputLogic outputLogic) {
        this.dataProvider = new DestinationDataProvider(dataService);
        this.viewLogic = new DestinationLogic(outputLogic, this);

        setSizeFull();

        HorizontalLayout topLayout = createTopBar();

        grid = new DestinationGrid();
        grid.setDataProvider(this.dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        dicomForm = new DestinationDicomForm(viewLogic);
        stowForm = new DestinationStowForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.setPadding(false);
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(dicomForm);
        add(stowForm);

        viewLogic.init(null);
    }

    public DestinationLogic getViewLogic() {
        return viewLogic;
    }

    protected void setForwardNode(ForwardNode forwardNode) {
        dataProvider.setForwardNode(forwardNode);
    }

    private HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter properties of destination");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newDestinationDicom = new Button("Dicom");
        newDestinationDicom.getElement().setAttribute("title", "New destination of type dicom");
        newDestinationDicom.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestinationDicom.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newDestinationDicom.addClickListener(click -> viewLogic.newDestinationDicom());

        newDestinationStow = new Button("Stow");
        newDestinationStow.getElement().setAttribute("title", "New destination of type stow");
        newDestinationStow.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestinationStow.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newDestinationStow.addClickListener(click -> viewLogic.newDestinationStow());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        topLayout.add(filter);
        topLayout.add(newDestinationDicom);
        topLayout.add(newDestinationStow);
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
        newDestinationDicom.setEnabled(enabled);
        newDestinationStow.setEnabled(enabled);
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
        showDicomForm(false);
        showStowForm(false);
    }

    protected void updateDestination(Destination data) {
        if (data.getForwardNode() != null) {
            NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
            viewLogic.getOutputLogic().getApplicationEventPublisher()
                .publishEvent(new OutputNodeEvent(data, eventType));
        }
        dataProvider.save(data);
        showDicomForm(false);
        showStowForm(false);
    }

    protected void removeDestination(Destination data) {
        if (data.getForwardNode() != null) {
            viewLogic.getOutputLogic().getApplicationEventPublisher()
                .publishEvent(new OutputNodeEvent(data, NodeEventType.REMOVE));
        }
        dataProvider.delete(data);
        showDicomForm(false);
        showStowForm(false);
    }

    protected void editDestination(Destination data) {
        if (data != null) {
            DestinationType type = data.getType();
            if (type != null) {
                switch (type) {
                    case dicom:
                        showDicomForm(true);
                        dicomForm.editDestination(data);
                        showStowForm(false);
                        stowForm.editDestination(null);
                        return;
                    case stow:
                        showDicomForm(false);
                        dicomForm.editDestination(null);
                        showStowForm(true);
                        stowForm.editDestination(data);
                        return;
                }
            }
        }

        showDicomForm(false);
        dicomForm.editDestination(null);
        showStowForm(false);
        stowForm.editDestination(null);
    }

    protected void showDicomForm(boolean show) {
        dicomForm.setVisible(show);
        dicomForm.setEnabled(show);
    }

    protected void showStowForm(boolean show) {
        stowForm.setVisible(show);
        stowForm.setEnabled(show);
    }

    public boolean hasChanges() {
        return dataProvider.hasChanges();
    }
}
