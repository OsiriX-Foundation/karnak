package org.karnak.ui.research;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Research;

public class NewResearchForm extends HorizontalLayout {
    private Binder<Research> binder;

    private Button buttonAdd;
    private TextField textResearchName;
    private TextField textSecret;

    public NewResearchForm() {
        setWidthFull();
        TextFieldsBindResearch textFieldsBindResearch = new TextFieldsBindResearch();
        binder = textFieldsBindResearch.getBinder();
        buttonAdd = new Button("Add");
        textResearchName = textFieldsBindResearch.getTextResearchName();
        textSecret = textFieldsBindResearch.getTextSecret();
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

    public Binder<Research> getBinder() {
        return binder;
    }
}
