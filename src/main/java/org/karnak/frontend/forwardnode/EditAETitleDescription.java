package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.ForwardNodeEntity;


public class EditAETitleDescription extends HorizontalLayout {
    private final TextField textFieldAETitle;
    private final TextField textFieldDescription;
    private final Binder<ForwardNodeEntity> binder;

    public EditAETitleDescription(Binder<ForwardNodeEntity> binder) {
        this.binder = binder;
        textFieldAETitle = new TextField("Forward AETitle");
        textFieldDescription = new TextField("Description");

        textFieldAETitle.setWidth("30%");
        textFieldDescription.setWidth("70%");
        add(textFieldAETitle, textFieldDescription);
        setBinder();
    }

    public void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        if (forwardNodeEntity != null) {
            binder.readBean(forwardNodeEntity);
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
            .withValidator(value -> value.length() <= 16,
                "Forward AETitle has more than 16 characters")
            .bind(ForwardNodeEntity::getFwdAeTitle, ForwardNodeEntity::setFwdAeTitle);
        binder.forField(textFieldDescription)
            .bind(ForwardNodeEntity::getDescription, ForwardNodeEntity::setDescription);
    }
}
