package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.karnak.data.gateway.ForwardNode;


public class EditAETitleDescription extends HorizontalLayout {
    private final TextField textFieldAETitle;
    private final TextField textFieldDescription;
    public EditAETitleDescription() {
        textFieldAETitle = new TextField("Forward AETitle *");
        textFieldDescription = new TextField("Description");

        textFieldAETitle.setWidth("30%");
        textFieldDescription.setWidth("70%");
        add(textFieldAETitle, textFieldDescription);
        /*
        textFieldAETitle.setRequired(true);
        textFieldAETitle.setValueChangeMode(ValueChangeMode.EAGER);
        textFieldAETitle.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        textFieldDescription.setValueChangeMode(ValueChangeMode.EAGER);
        textFieldDescription.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        textFieldDescription.addValueChangeListener(e -> {
            TextField tf = e.getSource();
            tf.getElement().setAttribute("title", tf.getValue());
        });
        */
    }

    public void setForwardNode(ForwardNode forwardNode) {
        if (forwardNode != null) {
            textFieldDescription.setValue(forwardNode.getDescription());
            textFieldAETitle.setValue(forwardNode.getFwdAeTitle());
        } else {
            textFieldDescription.clear();
            textFieldAETitle.clear();
        }
    }
}
