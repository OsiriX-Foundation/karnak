package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class WarningNoProjectsDefined extends Dialog {

  private final Button btnValidate;
  private final Button btnCancel;

  public WarningNoProjectsDefined() {
    btnValidate = new Button();
    btnCancel = new Button();

    setContent();
  }

  public void setTextBtnValidate(String text) {
    btnValidate.setText(text);
    }

    public void setTextBtnCancel(String text) {
        btnCancel.setText(text);
    }

    private void setContent() {
        removeAll();
        Div divTitle = new Div();
        divTitle.setText("No projects created");
        divTitle.getStyle()
                .set("font-size", "large").set("font-weight", "bolder")
                .set("padding-bottom", "10px").set("color", "red");

        Div divContent = new Div();
        Div divIntro = new Div();
        divIntro.setText("No projects are defined. You can't use the de-identification until you have created a project.");
        divIntro.getStyle().set("padding-bottom", "10px");
        divContent.add(divIntro);
        btnValidate.setWidthFull();
        btnCancel.setWidthFull();

        HorizontalLayout btnLayout = new HorizontalLayout(btnValidate, btnCancel);
        btnLayout.getStyle().set("margin-left", "50%");
        add(divTitle, divContent, btnLayout);
    }

    public Button getBtnValidate() {
        return btnValidate;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }
}
