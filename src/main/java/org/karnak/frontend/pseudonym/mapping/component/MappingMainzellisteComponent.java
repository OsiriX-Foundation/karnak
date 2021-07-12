/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping.component;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.backend.util.DateFormat;
import org.karnak.frontend.component.BoxShadowComponent;
import org.karnak.frontend.util.UIS;

public class MappingMainzellisteComponent extends VerticalLayout {

  // Components
  private TextField pseudonymTextField;
  private Button findButton;
  private Details patientFoundDetails;
  private Div resultDiv;

  public MappingMainzellisteComponent() {
    setWidthFull();
    HorizontalLayout pseudonymLayout = new HorizontalLayout();

    // Label Mainzelliste
    Label titleMainzelliste = new Label("Mainzelliste");
    titleMainzelliste.getElement().getStyle().set("margin-top", "6px");
    titleMainzelliste.getElement().getStyle().set("margin-right", "1%");
    titleMainzelliste.getStyle().set("font-size", "medium").set("font-weight", "bolder");

    // TextField input for pseudonym
    pseudonymTextField = new TextField();
    pseudonymTextField.setPlaceholder("Pseudonym");
    pseudonymTextField.setMinWidth(70, Unit.PERCENTAGE);

    // Find Button
    findButton = new Button("Find Patient");
    findButton.setAutofocus(true);

    // Pseudonym layout
    pseudonymLayout.setWidthFull();
    pseudonymLayout.add(titleMainzelliste, pseudonymTextField, findButton);
    BoxShadowComponent pseudonymBoxShadowComponent = new BoxShadowComponent(pseudonymLayout);
    pseudonymBoxShadowComponent.getElement().getStyle().set("padding", "1%");
    pseudonymBoxShadowComponent.getElement().getStyle().set("width", "44%");

    // Result
    patientFoundDetails = new Details();
    BoxShadowComponent patientFoundBoxShadowComponent = new BoxShadowComponent(patientFoundDetails);
    patientFoundBoxShadowComponent.getElement().getStyle().set("padding", "1%");
    patientFoundBoxShadowComponent.getElement().getStyle().set("width", "45%");
    resultDiv = new Div();
    resultDiv.getElement().getStyle().set("padding", "0%");
    resultDiv.getElement().getStyle().set("width", "45%");
    resultDiv.add(UIS.setWidthFull(patientFoundBoxShadowComponent));

    add(pseudonymBoxShadowComponent, resultDiv);
  }

  /**
   * Handle result when Find patient is clicked
   *
   * @param mainzellistePatientFound patient found/not found by the backend
   */
  public void handleResultFindPatient(MainzellistePatient mainzellistePatientFound) {
    // Reset details
    getPatientFoundDetails().setContent(null);
    getPatientFoundDetails().setSummary(null);

    // If found set patient data
    if (mainzellistePatientFound != null) {
      handleResultFindPatientPatientFound(mainzellistePatientFound);
    } else {
      // Else notification message
      handleResultFindPatientPatientNotFound();
    }
    getPseudonymTextField().clear();
  }

  /** Handle Result of Find Patient when Patient Not Found */
  private void handleResultFindPatientPatientNotFound() {
    getResultDiv().setVisible(false);
    Span content =
        new Span(String.format("Pseudonym %s not found", getPseudonymTextField().getValue()));
    content.getStyle().set("color", "var(--lumo-error-text-color)");
    Notification notification = new Notification(content);
    notification.setDuration(3000);
    notification.setPosition(Position.TOP_CENTER);
    notification.open();
  }

  /** Handle Result of Find Patient when Patient Found */
  private void handleResultFindPatientPatientFound(MainzellistePatient mainzellistePatientFound) {
    getResultDiv().setVisible(true);
    getPatientFoundDetails().setOpened(true);

    // Summary
    Label summaryDetails =
        new Label(String.format("Pseudonym %s", getPseudonymTextField().getValue()));
    summaryDetails.getStyle().set("font-size", "large").set("font-weight", "bolder");
    getPatientFoundDetails().setSummary(summaryDetails);

    // Add result in details
    VerticalLayout patientFoundDetailLayout = new VerticalLayout();
    patientFoundDetailLayout.setWidthFull();
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("Patient Id"), new Label(mainzellistePatientFound.getPatientId())));
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("Last Name"),
            new Label(mainzellistePatientFound.getPatientLastName())));
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("First Name"),
            new Label(mainzellistePatientFound.getPatientFirstName())));
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("Birth Date"),
            new Label(
                DateFormat.format(
                    mainzellistePatientFound.getPatientBirthDate(),
                    DateFormat.FORMAT_DDMMYYYY_SLASH))));
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("Issuer Of Patient Id"),
            new Label(mainzellistePatientFound.getIssuerOfPatientId())));
    patientFoundDetailLayout.add(
        new HorizontalLayout(
            buildBadgeTitle("Sex "), new Label(mainzellistePatientFound.getPatientSex())));

    // Add layout
    getPatientFoundDetails().addContent(patientFoundDetailLayout);
  }

  /**
   * Build badge title
   *
   * @param title Title
   * @return BoxShadowComponent designed with the title
   */
  private BoxShadowComponent buildBadgeTitle(final String title) {
    // Title
    Label label = new Label(title);
    label.getElement().getStyle().set("padding", "10px");

    // Badge
    BoxShadowComponent badgeTitle = new BoxShadowComponent(label);
    badgeTitle.setWidthFull();
    badgeTitle.getStyle().set("font-size", "revert").set("font-weight", "bolder");
    badgeTitle.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
    badgeTitle.getStyle().set("color", "var(--lumo-primary-text-color)");

    return badgeTitle;
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

  public Details getPatientFoundDetails() {
    return patientFoundDetails;
  }

  public void setPatientFoundDetails(Details patientFoundDetails) {
    this.patientFoundDetails = patientFoundDetails;
  }

  public Div getResultDiv() {
    return resultDiv;
  }

  public void setResultDiv(Div resultDiv) {
    this.resultDiv = resultDiv;
  }
}
