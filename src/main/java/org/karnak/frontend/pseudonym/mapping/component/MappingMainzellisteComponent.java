package org.karnak.frontend.pseudonym.mapping.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.frontend.component.BoxShadowComponent;

public class MappingMainzellisteComponent extends VerticalLayout {

  // Components
  private TextField pseudonymTextField;
  private Button findButton;
  private Label patientFoundLabel;

  public MappingMainzellisteComponent() {
    setWidthFull();

    HorizontalLayout pseudonymLayout = new HorizontalLayout();
    pseudonymTextField = new TextField();
    pseudonymTextField.setPlaceholder("Pseudonym");
    findButton = new Button("Find Patient");
    pseudonymLayout.setWidthFull();
    pseudonymLayout.add(pseudonymTextField, findButton);

    HorizontalLayout patientFoundLayout = new HorizontalLayout();
    patientFoundLabel = new Label();
    patientFoundLayout.add(patientFoundLabel);
    patientFoundLayout.setWidthFull();

    add(pseudonymLayout, new BoxShadowComponent(patientFoundLayout));
  }

  public TextField getPseudonymTextField() {
    return pseudonymTextField;
  }

  public void setPseudonymTextField(TextField pseudonymTextField) {
    this.pseudonymTextField = pseudonymTextField;
  }

  public Button getFindButton() {
    return findButton;
  }

  public void setFindButton(Button findButton) {
    this.findButton = findButton;
  }

  public Label getPatientFoundLabel() {
    return patientFoundLabel;
  }

  public void setPatientFoundLabel(Label patientFoundLabel) {
    this.patientFoundLabel = patientFoundLabel;
  }
}
