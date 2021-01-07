package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.DestinationDataProvider;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.frontend.component.ConfirmDialog;

public class NewUpdateDestination extends VerticalLayout {

    private final DestinationDataProvider destinationDataProvider;
    private final ViewLogic viewLogic;
    private final FormDICOM formDICOM;
    private final FormSTOW formSTOW;
    private Destination currentDestination;
    private final Binder<Destination> binderFormDICOM;
    private final Binder<Destination> binderFormSTOW;
    private final ButtonSaveDeleteCancel buttonDestinationDICOMSaveDeleteCancel;
    private final ButtonSaveDeleteCancel buttonDestinationSTOWSaveDeleteCancel;

    public NewUpdateDestination(DestinationDataProvider destinationDataProvider,
        ViewLogic viewLogic) {
        this.destinationDataProvider = destinationDataProvider;
        this.viewLogic = viewLogic;
        setSizeFull();
        binderFormDICOM = new BeanValidationBinder<>(Destination.class);
        binderFormSTOW = new BeanValidationBinder<>(Destination.class);
        buttonDestinationDICOMSaveDeleteCancel = new ButtonSaveDeleteCancel();
        buttonDestinationSTOWSaveDeleteCancel = new ButtonSaveDeleteCancel();
        formDICOM = new FormDICOM(binderFormDICOM, buttonDestinationDICOMSaveDeleteCancel);
        formSTOW = new FormSTOW(binderFormSTOW, buttonDestinationSTOWSaveDeleteCancel);
        currentDestination = null;

        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

    public void load(Destination destination, DestinationType type) {
        if (destination != null) {
            currentDestination = destination;
            buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(true);
            buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentDestination = type == DestinationType.stow ? Destination.ofStowEmpty() : Destination.ofDicomEmpty();
            buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(false);
            buttonDestinationSTOWSaveDeleteCancel.getDelete().setEnabled(false);
        }
        setView(type);
    }

    public void setView(DestinationType type) {
        removeAll();
        if (type == DestinationType.stow) {
            add(formSTOW);
            binderFormSTOW.readBean(currentDestination);
        } else if (type == DestinationType.dicom) {
            add(formDICOM);
            binderFormDICOM.readBean(currentDestination);
        }
    }

    private void setButtonSaveEvent() {
        buttonDestinationDICOMSaveDeleteCancel.getSave().addClickListener(event -> {
            if (currentDestination.getType() == DestinationType.dicom && binderFormDICOM.writeBeanIfValid(currentDestination)) {
                NodeEventType nodeEventType = currentDestination.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
            }
        });

        buttonDestinationSTOWSaveDeleteCancel.getSave().addClickListener(event -> {
            if (currentDestination.getType() == DestinationType.stow && binderFormSTOW.writeBeanIfValid(currentDestination)) {
                NodeEventType nodeEventType = currentDestination.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
                saveCurrentDestination(nodeEventType);
            }
        });
    }

    private void saveCurrentDestination(NodeEventType nodeEventType) {
        destinationDataProvider.save(currentDestination);
        viewLogic.updateForwardNodeInEditView();
        viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentDestination, nodeEventType));
    }

    private void setButtonDeleteEvent() {
        buttonDestinationDICOMSaveDeleteCancel.getDelete().addClickListener(event -> {
            removeCurrentDestination();
        });
        buttonDestinationSTOWSaveDeleteCancel.getDelete().addClickListener(event -> {
            removeCurrentDestination();
        });
    }

    private void removeCurrentDestination() {
        if (currentDestination != null) {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Are you sure to delete the forward node " + currentDestination.getDescription() +
                            " [" + currentDestination.getType() + "] ?");
            dialog.addConfirmationListener(componentEvent -> {
                NodeEvent nodeEvent = new NodeEvent(currentDestination, NodeEventType.REMOVE);
                destinationDataProvider.delete(currentDestination);
                viewLogic.getApplicationEventPublisher().publishEvent(nodeEvent);
                viewLogic.updateForwardNodeInEditView();
            });
            dialog.open();
        }
    }

    public Button getButtonDICOMCancel() {
        return buttonDestinationDICOMSaveDeleteCancel.getCancel();
    }

    public Button getButtonSTOWCancel() {
        return buttonDestinationSTOWSaveDeleteCancel.getCancel();
    }
}
