package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.forwardnode.extid.ExternalPseudonymView;
import org.karnak.ui.util.UIS;

public class LayoutDesidentification extends Div {
    private final Binder<Destination> destinationBinder;

    private Checkbox checkboxDesidentification;
    private Checkbox checkboxUsePseudonym;
    private ProfileDropDown profileDropDown;
    private ExternalPseudonymView externalPseudonymView;

    private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";
    private final String LABEL_CHECKBOX_EXTERNAL_PSEUDONYM = "Use external pseudonym";

    public LayoutDesidentification(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;

        setElements();
        setBinder();
        setEventCheckboxDesidentification();
        setEventCheckboxExternalPseudonym();
        add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, profileDropDown)));

        if (checkboxDesidentification.getValue()) {
            add(checkboxUsePseudonym);
        }
    }

    private void setElements() {
        checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
        profileDropDown = new ProfileDropDown();
        checkboxDesidentification.setValue(true);
        checkboxDesidentification.setMinWidth("25%");

        profileDropDown.setMinWidth("75%");

        checkboxUsePseudonym = new Checkbox(LABEL_CHECKBOX_EXTERNAL_PSEUDONYM);
        checkboxUsePseudonym.setMinWidth("25%");

        externalPseudonymView = new ExternalPseudonymView();
        externalPseudonymView.setMinWidth("70%");
    }

    private void setEventCheckboxDesidentification(){
        checkboxDesidentification.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                profileDropDown.setEnabled(event.getValue());
                if (event.getValue()){
                    add(checkboxUsePseudonym);
                } else {
                    checkboxUsePseudonym.clear();
                    externalPseudonymView.clear();
                    remove(checkboxUsePseudonym);
                    remove(externalPseudonymView);
                }
            }
        });
    }

    private void setEventCheckboxExternalPseudonym(){
        checkboxUsePseudonym.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue()){
                    add(externalPseudonymView);
                } else {
                    externalPseudonymView.clear();
                    remove(externalPseudonymView);
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
