package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.DataService;
import org.karnak.ui.gateway.DestinationDataProvider;
import org.karnak.ui.util.UIS;

public class DestinationsView extends VerticalLayout {
    private final DestinationDataProvider destinationDataProvider;

    private TextField filter;
    private Button newDestinationDICOM;
    private Button newDestinationStow;
    private GridDestination gridDestination;

    private HorizontalLayout layoutFilterButton;

    private String LABEL_NEW_DESTINATION_DICOM = "DICOM";
    private String LABEL_NEW_DESTINATION_STOW = "STOW";
    private String PLACEHOLDER_FILTER = "Filter properties of destination";

    public DestinationsView(DataService dataService) {
        setSizeFull();
        destinationDataProvider = new DestinationDataProvider(dataService);
        filter = new TextField();
        newDestinationDICOM = new Button(LABEL_NEW_DESTINATION_DICOM);
        newDestinationStow = new Button(LABEL_NEW_DESTINATION_STOW);
        gridDestination = new GridDestination();

        setTextFieldFilter();
        setButtonNewDestinationDICOM();
        setButtonNewDestinationSTOW();
        setForwardNode(null);

        layoutFilterButton = new HorizontalLayout(filter, newDestinationDICOM, newDestinationStow);
        layoutFilterButton.setVerticalComponentAlignment(Alignment.START, filter);
        layoutFilterButton.expand(filter);

        add(UIS.setWidthFull(layoutFilterButton),
                UIS.setWidthFull(gridDestination));
    }

    protected void setForwardNode(ForwardNode forwardNode) {
        if (forwardNode == null) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
        destinationDataProvider.setForwardNode(forwardNode);
        gridDestination.setDataProvider(destinationDataProvider);
    }

    private void setTextFieldFilter() {
        filter.setPlaceholder(PLACEHOLDER_FILTER);
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> destinationDataProvider.setFilter(event.getValue()));
    }

    private void setButtonNewDestinationDICOM() {
        newDestinationDICOM.getElement().setAttribute("title", "New destination of type dicom");
        newDestinationDICOM.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestinationDICOM.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        // newDestinationDICOM.addClickListener(click -> destinationLogic.newDestinationDicom());
    }

    private void setButtonNewDestinationSTOW() {
        newDestinationStow.getElement().setAttribute("title", "New destination of type stow");
        newDestinationStow.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestinationStow.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        // newDestinationStow.addClickListener(click -> destinationLogic.newDestinationStow());
    }


    public void setEnabled(boolean enabled) {
        filter.setEnabled(enabled);
        newDestinationDICOM.setEnabled(enabled);
        newDestinationStow.setEnabled(enabled);
        gridDestination.setEnabled(enabled);
    }

    public Button getNewDestinationDICOM() {
        return newDestinationDICOM;
    }

    public Button getNewDestinationStow() {
        return newDestinationStow;
    }

    public GridDestination getGridDestination() {
        return gridDestination;
    }
}
