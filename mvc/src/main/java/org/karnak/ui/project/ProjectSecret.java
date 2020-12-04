package org.karnak.ui.project;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.ui.component.WarningConfirmDialog;

public class ProjectSecret extends Div {
    private final String WARNING_TEXT = "If you change the project secret, the integrity of the DICOM will be compromise";
    private final String REFER_LINK_TEXT = "For more details on the use of the project secret, please refer to the following link";
    private final Anchor REFER_LINK = new Anchor("https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/rules#action-u-generate-a-new-uid",
            "How KARNAK does ?");

    private Div titleDiv = new Div();
    private Div valueDiv = new Div();
    private Div messageWarningLayout = new Div();
    private TextField textProjectSecret;
    private Button generateButton = new Button("Generate Secret");

    private String TITLE = "Project Secret";

    public ProjectSecret(TextField textProjectSecret) {
        this.textProjectSecret = textProjectSecret;

        setWidthFull();
        setTitle();
        setValue();
        setMessageWarningLayout();
        eventGenerateSecret();
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

    private void setMessageWarningLayout() {
        messageWarningLayout.add(new Div(new Text(WARNING_TEXT)));
        messageWarningLayout.add(new Div(new Text(REFER_LINK_TEXT)));
        messageWarningLayout.add(REFER_LINK);
    }

    public void clear() {
        textProjectSecret.clear();
    }

    private void eventGenerateSecret() {
        generateButton.addClickListener(event -> {
            WarningConfirmDialog dialog = new WarningConfirmDialog(
                    messageWarningLayout
            );
            dialog.addConfirmationListener(componentEvent -> {
                String generateSecret = HMAC.byteToHex(HMAC.generateRandomKey());
                textProjectSecret.setValue(HMAC.showHexKey(generateSecret));
            });
            dialog.open();
        });
    }
}
