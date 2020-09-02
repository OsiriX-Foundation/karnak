package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.karnak.ui.component.ConfirmDialog;

public class ButtonSaveDeleteCancel extends HorizontalLayout {
    private Button save;
    private Button delete;
    private Button cancel;

    private String LABEL_SAVE = "Save";
    private String LABEL_CANCEL = "Cancel";
    private String LABEL_DELETE = "Delete";

    public ButtonSaveDeleteCancel() {
        save = new Button(LABEL_SAVE);
        cancel = new Button(LABEL_CANCEL);
        delete = new Button(LABEL_DELETE);

        add(save, delete, cancel);
        setButtonSave();
        setButtonCancel();
        setButtonDelete();
    }

    private void setButtonSave() {
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
        /*
        save.addClickListener(event -> {
            saveForwardNode();
        });
        */
    }
    private void setButtonCancel() {
        cancel.setWidth("100%");
        cancel.addClickShortcut(Key.ESCAPE);
        /*
        cancel.addClickListener(event -> this.gatewayViewLogic.cancelForwardNode());
        getElement().addEventListener("keydown", event -> this.gatewayViewLogic.cancelForwardNode())
                .setFilter("event.key == 'Escape'");
        */
    }

    private void setButtonDelete() {
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        /*
        delete.addClickListener(event -> {
            if (currentForwardNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the forward node " + currentForwardNode.getFwdAeTitle() + " ?");
                dialog.addConfirmationListener(componentEvent -> this.gatewayViewLogic.deleteForwardNode(currentForwardNode));
                dialog.open();
            }
        });
        */
    }

    public void setEnabled(boolean enabled) {
        save.setEnabled(enabled);
        delete.setEnabled(enabled);
        cancel.setEnabled(enabled);
    }

    public Button getSave() {
        return save;
    }

    public Button getDelete() {
        return delete;
    }

    public Button getCancel() {
        return cancel;
    }
}
