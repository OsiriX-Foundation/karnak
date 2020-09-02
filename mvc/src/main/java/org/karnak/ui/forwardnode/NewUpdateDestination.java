package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.ui.util.UIS;

public class NewUpdateDestination extends VerticalLayout {
    private FormDICOM formDICOM;
    private FormSTOW formSTOW;
    private Destination currentDestination;
    private Binder<Destination> binderFormDICOM;
    private Binder<Destination> binderFormSTOW;
    private ButtonSaveDeleteCancel buttonDestinationSaveDeleteCancel;

    public NewUpdateDestination() {
        setSizeFull();
        binderFormDICOM = new BeanValidationBinder<>(Destination.class);
        binderFormSTOW = new BeanValidationBinder<>(Destination.class);
        formDICOM = new FormDICOM(binderFormDICOM);
        formSTOW = new FormSTOW(binderFormSTOW);
        currentDestination = Destination.ofDicomEmpty();
        buttonDestinationSaveDeleteCancel = new ButtonSaveDeleteCancel();
        setBinderEvent();
    }

    public void setView(DestinationType type) {
        removeAll();
        if (type == DestinationType.stow) {
            add(formSTOW);
        } else if (type == DestinationType.dicom) {
            add(formDICOM);
        }
        add(UIS.setWidthFull(buttonDestinationSaveDeleteCancel));
    }

    public void setBinder(DestinationType type) {
        if (type == DestinationType.stow) {
            binderFormSTOW.readBean(currentDestination);
        } else if (type == DestinationType.dicom) {
            binderFormDICOM.readBean(currentDestination);
        }
    }

    public void load(Destination destination) {
        if (destination != null) {
            setView(destination.getType());
            currentDestination = destination;
            buttonDestinationSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentDestination = Destination.ofDicomEmpty();
            buttonDestinationSaveDeleteCancel.getDelete().setEnabled(false);
        }
        setBinder(destination.getType());
    }

    private void setBinderEvent() {
        binderFormDICOM.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderFormDICOM.hasChanges();
            buttonDestinationSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
    }
}
