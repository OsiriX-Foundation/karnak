package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ProfileMetadata extends VerticalLayout {
    private Div titleDiv = new Div();
    private Div valueDiv = new Div();
    private TextField valueField = new TextField();
    private Button editButton = new Button(new Icon(VaadinIcon.EDIT));
    private Button validateEditButton = new Button(new Icon(VaadinIcon.CHECK));
    private Button disabledEditButton = new Button(new Icon(VaadinIcon.CLOSE));

    private String title;
    private String value;

    private boolean inputEnable = false;

    public ProfileMetadata(String title, String value) {
        this.title = title;
        this.value = value;

        setTitleText();
        setValueText();

        setStyle();

        titleDiv.add(editButton);

        editButton.addClickListener(event -> {
            editOnClick();
        });

        disabledEditButton.addClickListener(event -> {
            disabledEditButton();
        });

        validateEditButton.addClickListener(event -> {
            validateEditButton();
        });

        add(titleDiv, valueDiv);
    }

    private void setStyle() {
        titleDiv.getStyle().set("font-weight", "bold").set("margin-top", "0px").set("padding-left", "5px");
        valueDiv.getStyle().set("color", "grey").set("padding-left", "10px").set("margin-top", "5px");
    }

    private void setTitleText() {
        titleDiv.setText(this.title);
    }

    private void setValueText() {
        Text valueText = new Text(this.value);
        valueDiv.add(valueText);
    }

    private void setValueTextField() {
        valueField.setValue(this.value);
        valueDiv.add(valueField);
        valueDiv.add(validateEditButton);
        valueDiv.add(disabledEditButton);
    }

    private void editOnClick() {
        titleDiv.remove(editButton);
        valueDiv.removeAll();
        setValueTextField();
    }

    private void disabledEditButton() {
        titleDiv.add(editButton);
        valueDiv.removeAll();
        setValueText();
    }

    private void validateEditButton() {
        titleDiv.add(editButton);
        this.value = valueField.getValue();
        valueDiv.removeAll();
        setValueText();
    }

    public String getValue() {
        return value;
    }

    public Button getValidateEditButton() {
        return validateEditButton;
    }
}
