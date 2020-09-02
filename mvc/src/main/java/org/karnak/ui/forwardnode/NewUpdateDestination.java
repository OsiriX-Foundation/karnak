package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
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

    // TODO: Use enumerate?
    public void setView(String type) {
        removeAll();
        if (type == "STOW") {
            //TODO: implement formSTOW
            // add(formSTOW);
        } else if (type == "DICOM") {
            add(formDICOM);
        }
        add(UIS.setWidthFull(buttonDestinationSaveDeleteCancel));
    }
}
