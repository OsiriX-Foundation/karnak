package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.ui.gateway.DestinationDataProvider;
import org.karnak.ui.util.UIS;

public class NewUpdateDestination extends VerticalLayout {
    private DestinationDataProvider destinationDataProvider;
    private DestinationViewLogic destinationViewLogic;
    private FormDICOM formDICOM;
    private FormSTOW formSTOW;
    private Destination currentDestination;
    private Binder<Destination> binderFormDICOM;
    private Binder<Destination> binderFormSTOW;
    private ButtonSaveDeleteCancel buttonDestinationSaveDeleteCancel;

    public NewUpdateDestination(DestinationDataProvider destinationDataProvider, DestinationViewLogic destinationViewLogic) {
        this.destinationDataProvider = destinationDataProvider;
        this.destinationViewLogic = destinationViewLogic;
        setSizeFull();
        binderFormDICOM = new BeanValidationBinder<>(Destination.class);
        binderFormSTOW = new BeanValidationBinder<>(Destination.class);
        formDICOM = new FormDICOM(binderFormDICOM);
        formSTOW = new FormSTOW(binderFormSTOW);
        currentDestination = null;
        buttonDestinationSaveDeleteCancel = new ButtonSaveDeleteCancel();

        setBinderEvent();
        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

    public void load(Destination destination, DestinationType type) {
        if (destination != null) {
            currentDestination = destination;
            buttonDestinationSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentDestination = type == DestinationType.stow ? Destination.ofStowEmpty() : Destination.ofDicomEmpty();
            buttonDestinationSaveDeleteCancel.getDelete().setEnabled(false);
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
        add(UIS.setWidthFull(buttonDestinationSaveDeleteCancel));
    }

    private void setBinderEvent() {
        /*
        binderFormDICOM.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderFormDICOM.hasChanges();
            buttonDestinationSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });

        binderFormSTOW.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderFormDICOM.hasChanges();
            buttonDestinationSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
        */
    }

    private void setButtonSaveEvent() {
        buttonDestinationSaveDeleteCancel.getSave().addClickListener(event -> {
            NodeEventType nodeEventType = currentDestination.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
            if (currentDestination.getType() == DestinationType.stow && binderFormSTOW.writeBeanIfValid(currentDestination)) {
                destinationDataProvider.save(currentDestination);
                destinationViewLogic.updateForwardNodeInEditView();
                destinationViewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentDestination, nodeEventType));
            }

            if (currentDestination.getType() == DestinationType.dicom && binderFormDICOM.writeBeanIfValid(currentDestination)) {
                destinationDataProvider.save(currentDestination);
                destinationViewLogic.updateForwardNodeInEditView();
                destinationViewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentDestination, nodeEventType));
            }
        });
    }

    private void setButtonDeleteEvent() {
        buttonDestinationSaveDeleteCancel.getDelete().addClickListener(event -> {
            NodeEvent nodeEvent = new NodeEvent(currentDestination, NodeEventType.REMOVE);
            destinationDataProvider.delete(currentDestination);
            destinationViewLogic.getApplicationEventPublisher().publishEvent(nodeEvent);
            destinationViewLogic.updateForwardNodeInEditView();
        });
    }

    public Button getButtonCancel() {
        return buttonDestinationSaveDeleteCancel.getCancel();
    }
}
