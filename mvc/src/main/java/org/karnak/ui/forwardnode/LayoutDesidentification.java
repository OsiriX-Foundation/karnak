package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;
import org.karnak.ui.util.UIS;

public class LayoutDesidentification extends Div {
    private final Binder<Destination> destinationBinder;

    private Checkbox checkboxDesidentification;
    private Checkbox checkboxUseAsPatientName;
    private ProfileDropDown profileDropDown;
    private ProjectDropDown projectDropDown;
    private ExtidPresentInDicomTagView extidPresentInDicomTagView;
    private Div div;

    private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";

    private Select<String> extidListBox;
    final String [] extidSentence = {"Pseudonym are generate automatically","Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};

    public LayoutDesidentification(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;
        profileDropDown = new ProfileDropDown();
        projectDropDown = new ProjectDropDown();

        setElements();
        setBinder();
        setEventCheckboxDesidentification();
        setEventExtidListBox();

        div.add(profileDropDown);
        add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, div)));

        if (checkboxDesidentification.getValue()) {
            div.add(projectDropDown, extidListBox);
        }
    }

    private void setElements() {
        checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
        checkboxDesidentification.setValue(true);
        checkboxDesidentification.setMinWidth("25%");

        profileDropDown.setLabel("Choose a de-identification profile");
        profileDropDown.setWidth("100%");

        projectDropDown.setLabel("Choose a project");
        projectDropDown.setWidth("100%");

        extidListBox = new Select<>();
        extidListBox.setLabel("Pseudonym type");
        extidListBox.setWidth("100%");
        extidListBox.getStyle().set("right", "0px");
        extidListBox.setItems(extidSentence);

        checkboxUseAsPatientName = new Checkbox("Use as Patient Name");

        extidPresentInDicomTagView = new ExtidPresentInDicomTagView(destinationBinder);
        div = new Div();
        div.setWidth("100%");

    }

    private void setEventCheckboxDesidentification(){
        checkboxDesidentification.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                profileDropDown.setEnabled(event.getValue());
                if (event.getValue()){
                    div.add(projectDropDown, extidListBox);
                } else {
                    div.remove(projectDropDown);
                    extidListBox.setValue(extidSentence[0]);
                    checkboxUseAsPatientName.clear();
                    extidPresentInDicomTagView.clear();
                    div.remove(extidListBox);
                    remove(checkboxUseAsPatientName);
                    div.remove(extidPresentInDicomTagView);
                }
            }
        });
    }

    private void setEventExtidListBox() {
        extidListBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue().equals(extidSentence[0])) {
                    checkboxUseAsPatientName.clear();
                    extidPresentInDicomTagView.clear();
                    div.remove(checkboxUseAsPatientName);
                    div.remove(extidPresentInDicomTagView);
                } else {
                    div.add(UIS.setWidthFull(checkboxUseAsPatientName));
                    if (event.getValue().equals(extidSentence[1])) {
                        extidPresentInDicomTagView.clear();
                        div.remove(extidPresentInDicomTagView);
                    } else {
                        extidPresentInDicomTagView.enableComponent();
                        div.add(extidPresentInDicomTagView);
                    }
                }
            }
        });
    }


    private void setBinder() {
        destinationBinder.forField(checkboxDesidentification)
                .bind(Destination::getDesidentification, Destination::setDesidentification);
        destinationBinder.forField(profileDropDown)
                .withValidator(profilePipe -> profilePipe != null ||
                                (profilePipe == null && checkboxDesidentification.getValue() == false),
                        "Choose the de-identification profile\n")
                .bind(Destination::getProfile, Destination::setProfile);
        destinationBinder.forField(projectDropDown)
                .withValidator(project ->
                        project != null || (project == null && checkboxDesidentification.getValue() == false),
                        "Choose a project")
                .bind(Destination::getProject, Destination::setProject);

        destinationBinder.forField(extidListBox)
                .withValidator(type -> type != null,"Choose pseudonym type\n")
                .bind(destination -> {
                    if (destination.getIdTypes().equals(IdTypes.PID)){
                        return extidSentence[0];
                    } else if(destination.getIdTypes().equals(IdTypes.EXTID)) {
                        return extidSentence[1];
                    } else {
                        return extidSentence[2];
                    }
                }, (destination, s) -> {
                    if (s.equals(extidSentence[0])) {
                        destination.setIdTypes(IdTypes.PID);
                    } else if (s.equals(extidSentence[1])){
                        destination.setIdTypes(IdTypes.EXTID);
                    } else {
                        destination.setIdTypes(IdTypes.ADD_EXTID);
                    }
                });

        destinationBinder.forField(checkboxUseAsPatientName)
                .bind(Destination::getPseudonymAsPatientName, Destination::setPseudonymAsPatientName);
    }
}
