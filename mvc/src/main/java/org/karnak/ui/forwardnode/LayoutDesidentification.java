package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.forwardnode.ExternalPseudonymView;
import org.karnak.ui.gateway.ProfileDropDown;
import org.karnak.ui.util.UIS;

public class LayoutDesidentification extends Div {
    private final Binder<Destination> destinationBinder;

    private final Checkbox checkboxDesidentification;
    private final ProfileDropDown profileDropDown;
    private ExternalPseudonymView externalPseudonymView;

    private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";

    public LayoutDesidentification(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;
        checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
        profileDropDown = new ProfileDropDown();
        setElements();
        setBinder();
        add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, profileDropDown)));
        add(externalPseudonymView);
    }

    private void setElements() {
        checkboxDesidentification.setValue(true);
        checkboxDesidentification.setMinWidth("25%");

        profileDropDown.setMinWidth("75%");

        externalPseudonymView = new ExternalPseudonymView(destinationBinder);
        externalPseudonymView.setMinWidth("70%");

        checkboxDesidentification.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                profileDropDown.setEnabled(event.getValue());
                if (event.getValue()){
                    externalPseudonymView.setVisible(true);
                } else {
                    externalPseudonymView.setVisible(false);
                    externalPseudonymView.disableDesidentification();
                }
            }
        });
    }


    private void setBinder() {
        destinationBinder.forField(checkboxDesidentification)
                .bind(destination -> {
                    final boolean desidentification = destination.getDesidentification();
                    if (desidentification){
                        externalPseudonymView.setVisible(true);
                    } else {
                        externalPseudonymView.setVisible(false);
                    }
                    return desidentification;
                }, Destination::setDesidentification);
        destinationBinder.forField(profileDropDown)
                .withValidator(profilePipe -> profilePipe != null ||
                                (profilePipe == null && checkboxDesidentification.getValue() == false),
                        "Choose the de-identification profile\n")
                .bind(Destination::getProfile, Destination::setProfile);
    }
}
