package org.karnak.ui.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Project;

public class NewProjectForm extends HorizontalLayout {
    private Binder<Project> binder;

    private Button buttonAdd;
    private TextField textResearchName;
    private TextField textSecret;

    public NewProjectForm() {
        setWidthFull();
        TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
        binder = textFieldsBindProject.getBinder();
        buttonAdd = new Button("Add");
        textResearchName = textFieldsBindProject.getTextResearchName();
        textSecret = textFieldsBindProject.getTextSecret();
        setElements();

        add(textResearchName, textSecret, buttonAdd);
        binder.bindInstanceFields(this);
    }

    private void setElements() {
        textResearchName.setWidth("20%");
        textResearchName.getStyle().set("padding-right", "10px");
        textResearchName.setPlaceholder("Research Name");
        textSecret.setWidth("20%");
        textSecret.getStyle().set("padding-right", "10px");
        textSecret.setPlaceholder("Secret");
    }

    public Button getButtonAdd() {
        return buttonAdd;
    }

    public void clear() {
        textResearchName.clear();
        textSecret.clear();
    }

    public Binder<Project> getBinder() {
        return binder;
    }
}
