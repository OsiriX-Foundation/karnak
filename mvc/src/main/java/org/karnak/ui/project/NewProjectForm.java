package org.karnak.ui.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Project;
import org.karnak.ui.forwardnode.ProfileDropDown;

public class NewProjectForm extends HorizontalLayout {
    private Binder<Project> binder;

    private Button buttonAdd;
    private TextField textResearchName;
    private ProfileDropDown profileDropDown;

    public NewProjectForm() {
        setWidthFull();
        TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
        binder = textFieldsBindProject.getBinder();
        buttonAdd = new Button("Add");
        textResearchName = textFieldsBindProject.getTextResearchName();
        profileDropDown = textFieldsBindProject.getProfileDropDown();
        setElements();

        add(textResearchName, profileDropDown, buttonAdd);
        binder.removeBinding(textFieldsBindProject.getTextSecret());
        binder.bindInstanceFields(this);
    }

    private void setElements() {
        textResearchName.setWidth("20%");
        textResearchName.getStyle().set("padding-right", "10px");
        textResearchName.setPlaceholder("Research Name");
    }

    public Button getButtonAdd() {
        return buttonAdd;
    }

    public void clear() {
        binder.readBean(new Project());
    }

    public Binder<Project> getBinder() {
        return binder;
    }
}
