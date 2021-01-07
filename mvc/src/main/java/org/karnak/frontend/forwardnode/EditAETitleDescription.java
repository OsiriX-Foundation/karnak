package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.ForwardNode;


public class EditAETitleDescription extends HorizontalLayout {
    private final TextField textFieldAETitle;
    private final TextField textFieldDescription;
    private final Binder<ForwardNode> binder;

    public EditAETitleDescription(Binder<ForwardNode> binder) {
        this.binder = binder;
        textFieldAETitle = new TextField("Forward AETitle");
        textFieldDescription = new TextField("Description");

        textFieldAETitle.setWidth("30%");
        textFieldDescription.setWidth("70%");
        add(textFieldAETitle, textFieldDescription);
        setBinder();
    }

    public void setForwardNode(ForwardNode forwardNode) {
        if (forwardNode != null) {
            binder.readBean(forwardNode);
            setEnabled(true);
        } else {
            binder.readBean(null);
            textFieldDescription.clear();
            textFieldAETitle.clear();
            setEnabled(false);
        }
    }

    public void setEnabled(boolean enabled) {
        textFieldAETitle.setEnabled(enabled);
        textFieldDescription.setEnabled(enabled);
    }

    private void setBinder() {
        binder.forField(textFieldAETitle)
                .withValidator(value -> !value.equals(""), "Forward AE Title is mandatory")
                .withValidator(value -> value.length() <= 16, "Forward AETitle has more than 16 characters")
                .bind(ForwardNode::getFwdAeTitle, ForwardNode::setFwdAeTitle);
        binder.forField(textFieldDescription)
                .bind(ForwardNode::getDescription, ForwardNode::setDescription);
    }
}
