package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
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

    public void load(Destination destination, DestinationType type) {
        if (destination != null) {
            currentDestination = destination;
            buttonDestinationSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentDestination = Destination.ofDicomEmpty();
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
        binderFormDICOM.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binderFormDICOM.hasChanges();
            buttonDestinationSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
    }

    public Button getButtonCancel() {
        return buttonDestinationSaveDeleteCancel.getCancel();
    }
}
