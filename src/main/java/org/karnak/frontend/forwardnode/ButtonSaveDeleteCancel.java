package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ButtonSaveDeleteCancel extends HorizontalLayout {

  private final Button save;
  private final Button delete;
  private final Button cancel;

  private final String LABEL_SAVE = "Save";
  private final String LABEL_CANCEL = "Cancel";
  private final String LABEL_DELETE = "Delete";

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
    }
    private void setButtonCancel() {
        cancel.setWidth("100%");
    }

    private void setButtonDelete() {
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
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
