package org.karnak.ui.research;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Research;

public class TextFieldsBindResearch {
    private Binder<Research> binder;

    private TextField textResearchName;
    private TextField textSecret;

    public TextFieldsBindResearch() {
        textResearchName = new TextField();
        textSecret = new TextField();

        binder = setBinder();
    }

    private Binder<Research> setBinder() {
        Binder<Research> binder = new BeanValidationBinder<>(Research.class);
        binder.forField(textResearchName)
                .withValidator(StringUtils::isNotBlank,"Research name is mandatory")
                .bind(Research::getName, Research::setName);
        binder.forField(textSecret)
                .withValidator(StringUtils::isNotBlank,"Secret is mandatory")
                .bind(Research::getSecret, Research::setSecret);
        return binder;
    }

    public Binder<Research> getBinder() {
        return binder;
    }

    public TextField getTextResearchName() {
        return textResearchName;
    }

    public TextField getTextSecret() {
        return textSecret;
    }
}
