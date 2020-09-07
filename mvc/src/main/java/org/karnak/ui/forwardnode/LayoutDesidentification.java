package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.gateway.ProfileDropDown;

public class LayoutDesidentification extends HorizontalLayout {
    private final Binder<Destination> destinationBinder;

    private final Checkbox checkboxDesidentification;
    private final ProfileDropDown profileDropDown;

    private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";

    public LayoutDesidentification(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;
        checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
        profileDropDown = new ProfileDropDown();
        setElements();
        setBinder();
        add(checkboxDesidentification, profileDropDown);
    }

    private void setElements() {
        checkboxDesidentification.setValue(true);
        checkboxDesidentification.setMinWidth("25%");

        profileDropDown.setMinWidth("75%");

        checkboxDesidentification.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                profileDropDown.setEnabled(event.getValue());
            }
        });
    }


    private void setBinder() {
        destinationBinder.forField(checkboxDesidentification) //
                .bind(Destination::getDesidentification, Destination::setDesidentification);
        destinationBinder.forField(profileDropDown)
                .withValidator(profilePipe -> profilePipe != null ||
                                (profilePipe == null && checkboxDesidentification.getValue() == false),
                        "Choose the de-identification profile\n")
                .bind(Destination::getProfile, Destination::setProfile);
    }
}
