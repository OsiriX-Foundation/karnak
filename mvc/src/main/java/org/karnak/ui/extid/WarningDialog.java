package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;

public class WarningDialog extends Dialog {

    public WarningDialog(String title, String text, String buttonText) {
        removeAll();
        Div divTitle = new Div();
        divTitle.setText(title);
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px");

        Div divContent = new Div();
        Div divIntro = new Div();
        divIntro.setText(text);
        divIntro.getStyle().set("padding-bottom", "10px");

        divContent.add(divIntro);

        Button cancelButton = new Button(buttonText, event -> {
            close();
        });

        cancelButton.getStyle().set("margin-left", "75%");
        add(divTitle, divContent, cancelButton);
    }
}
