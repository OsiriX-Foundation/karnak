package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.data.entity.ForwardNode;
import org.karnak.backend.service.DataService;
import org.karnak.backend.service.DestinationDataProvider;
import org.karnak.frontend.util.UIS;

public class DestinationsView extends VerticalLayout {

    private final DestinationDataProvider destinationDataProvider;

    private final TextField filter;
    private final Button newDestinationDICOM;
    private final Button newDestinationSTOW;
    private final GridDestination gridDestination;

    private final HorizontalLayout layoutFilterButton;

    private final String LABEL_NEW_DESTINATION_DICOM = "DICOM";
    private final String LABEL_NEW_DESTINATION_STOW = "STOW";
    private final String PLACEHOLDER_FILTER = "Filter properties of destination";

    public DestinationsView(DataService dataService) {
        setSizeFull();
        destinationDataProvider = new DestinationDataProvider(dataService);
        filter = new TextField();
        newDestinationDICOM = new Button(LABEL_NEW_DESTINATION_DICOM);
        newDestinationSTOW = new Button(LABEL_NEW_DESTINATION_STOW);
        gridDestination = new GridDestination();

        setTextFieldFilter();
        setButtonNewDestinationDICOM();
        setButtonNewDestinationSTOW();
        setForwardNode(null);

        layoutFilterButton = new HorizontalLayout(filter, newDestinationDICOM, newDestinationSTOW);
        layoutFilterButton.setVerticalComponentAlignment(Alignment.START, filter);
        layoutFilterButton.expand(filter);

        add(UIS.setWidthFull(layoutFilterButton),
                UIS.setWidthFull(gridDestination));
    }

    protected void setForwardNode(ForwardNode forwardNode) {
        setEnabled(forwardNode != null);
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
        newDestinationSTOW.getElement().setAttribute("title", "New destination of type stow");
        newDestinationSTOW.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newDestinationSTOW.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        // newDestinationStow.addClickListener(click -> destinationLogic.newDestinationStow());
    }


    public void setEnabled(boolean enabled) {
        filter.setEnabled(enabled);
        newDestinationDICOM.setEnabled(enabled);
        newDestinationSTOW.setEnabled(enabled);
        gridDestination.setEnabled(enabled);
    }

    public Button getNewDestinationDICOM() {
        return newDestinationDICOM;
    }

    public Button getNewDestinationSTOW() {
        return newDestinationSTOW;
    }

    public GridDestination getGridDestination() {
        return gridDestination;
    }
}
