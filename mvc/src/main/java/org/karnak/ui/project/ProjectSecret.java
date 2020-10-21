package org.karnak.ui.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.profilepipe.utils.HMAC;

public class ProjectSecret extends Div {
    private Div titleDiv = new Div();
    private Div valueDiv = new Div();
    private TextField textProjectSecret;
    private Button generateButton = new Button("Regenerate Secret");

    private String TITLE = "Project Secret";

    public ProjectSecret(TextField textProjectSecret) {
        this.textProjectSecret = textProjectSecret;

        setWidthFull();
        setTitle();
        setValue();
        eventRegenerateSecret();
        add(titleDiv, valueDiv);
    }

    private void setTitle() {
        titleDiv.setText(TITLE);
    }

    private void setValue() {
        textProjectSecret.getStyle().set("width", "80%");
        generateButton.getStyle().set("margin-left", "10px");
        valueDiv.add(textProjectSecret, generateButton);
    }

    public void clear() {
        textProjectSecret.clear();
    }

    private void eventRegenerateSecret() {
        generateButton.addClickListener(event -> {
            String generateSecret = HMAC.byteToHex(HMAC.generateRandomKey());
            textProjectSecret.setValue(HMAC.showHexKey(generateSecret));
        });
    }
}
