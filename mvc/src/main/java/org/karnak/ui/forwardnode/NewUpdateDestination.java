package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.data.DestinationDataProvider;
import org.karnak.ui.util.UIS;

public class NewUpdateDestination extends VerticalLayout {
    private DestinationDataProvider destinationDataProvider;
    private ViewLogic viewLogic;
    private FormDICOM formDICOM;
    private FormSTOW formSTOW;
    private Destination currentDestination;
    private Binder<Destination> binderFormDICOM;
    private Binder<Destination> binderFormSTOW;
    private ButtonSaveDeleteCancel buttonDestinationDICOMSaveDeleteCancel;

    public NewUpdateDestination(DestinationDataProvider destinationDataProvider, ViewLogic viewLogic) {
        this.destinationDataProvider = destinationDataProvider;
        this.viewLogic = viewLogic;
        setSizeFull();
        binderFormDICOM = new BeanValidationBinder<>(Destination.class);
        binderFormSTOW = new BeanValidationBinder<>(Destination.class);
        buttonDestinationDICOMSaveDeleteCancel = new ButtonSaveDeleteCancel();
        formDICOM = new FormDICOM(binderFormDICOM, buttonDestinationDICOMSaveDeleteCancel);
        formSTOW = new FormSTOW(binderFormSTOW);
        currentDestination = null;

        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

    public void load(Destination destination, DestinationType type) {
        if (destination != null) {
            currentDestination = destination;
            buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentDestination = type == DestinationType.stow ? Destination.ofStowEmpty() : Destination.ofDicomEmpty();
            buttonDestinationDICOMSaveDeleteCancel.getDelete().setEnabled(false);
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
            NodeEventType nodeEventType = currentDestination.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
            if (currentDestination.getType() == DestinationType.stow && binderFormSTOW.writeBeanIfValid(currentDestination)) {
                destinationDataProvider.save(currentDestination);
                viewLogic.updateForwardNodeInEditView();
                viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentDestination, nodeEventType));
            }

            if (currentDestination.getType() == DestinationType.dicom && binderFormDICOM.writeBeanIfValid(currentDestination)) {
                destinationDataProvider.save(currentDestination);
                viewLogic.updateForwardNodeInEditView();
                viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentDestination, nodeEventType));
            }
        });
    }

    private void setButtonDeleteEvent() {
        buttonDestinationDICOMSaveDeleteCancel.getDelete().addClickListener(event -> {

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
        });
    }

    public Button getButtonDICOMCancel() {
        return buttonDestinationDICOMSaveDeleteCancel.getCancel();
    }
}
