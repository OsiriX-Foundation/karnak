package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DestinationType;
import org.karnak.ui.util.UIS;

public class NewUpdateDestination extends VerticalLayout {
    private FormDICOM formDICOM;
    private Binder<Destination> binderFormDICOM;
    private ButtonSaveDeleteCancel buttonDestinationSaveDeleteCancel;

    public NewUpdateDestination() {
        setSizeFull();
        binderFormDICOM = new BeanValidationBinder<>(Destination.class);
        formDICOM = new FormDICOM(binderFormDICOM);
        buttonDestinationSaveDeleteCancel = new ButtonSaveDeleteCancel();
    }

    public void setView(DestinationType type) {
        removeAll();
        if (type == DestinationType.stow) {
            //TODO: implement formSTOW
            // add(formSTOW);
        } else if (type == DestinationType.dicom) {
            add(formDICOM);
        }
        add(UIS.setWidthFull(buttonDestinationSaveDeleteCancel));
    }

    public void load(Destination destination) {
        if (destination != null) {
            setView(destination.getType());
            binderFormDICOM.readBean(destination);
        } else {
            binderFormDICOM.readBean(Destination.ofDicomEmpty());
        }
    }
}
