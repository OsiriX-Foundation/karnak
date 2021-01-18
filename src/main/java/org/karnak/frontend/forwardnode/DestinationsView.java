package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.DestinationService;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class DestinationsView extends VerticalLayout {

    private final DestinationService destinationService;

    private TextField filter;
    private Button newDestinationDICOM;
    private Button newDestinationSTOW;
    private GridDestination gridDestination;

    private HorizontalLayout layoutFilterButton;

    private final String LABEL_NEW_DESTINATION_DICOM = "DICOM";
    private final String LABEL_NEW_DESTINATION_STOW = "STOW";
    private final String PLACEHOLDER_FILTER = "Filter properties of destination";

    @Autowired
    public DestinationsView(final DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostConstruct
    public void init() {
        setSizeFull();
        this.filter = new TextField();
        this.newDestinationDICOM = new Button(LABEL_NEW_DESTINATION_DICOM);
        this.newDestinationSTOW = new Button(LABEL_NEW_DESTINATION_STOW);
        this.gridDestination = new GridDestination();

        setTextFieldFilter();
        setButtonNewDestinationDICOM();
        setButtonNewDestinationSTOW();
        setForwardNode(null);

        this.layoutFilterButton = new HorizontalLayout(this.filter, this.newDestinationDICOM,
            this.newDestinationSTOW);
        this.layoutFilterButton.setVerticalComponentAlignment(Alignment.START, this.filter);
        this.layoutFilterButton.expand(this.filter);

        add(UIS.setWidthFull(this.layoutFilterButton),
            UIS.setWidthFull(this.gridDestination));
    }


    protected void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        setEnabled(forwardNodeEntity != null);
        destinationService.setForwardNode(forwardNodeEntity);
        gridDestination.setDataProvider(destinationService);
    }

    private void setTextFieldFilter() {
        filter.setPlaceholder(PLACEHOLDER_FILTER);
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> destinationService.setFilter(event.getValue()));
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
