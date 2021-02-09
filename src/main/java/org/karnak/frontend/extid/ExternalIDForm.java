/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.CachedPatient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalIDForm extends VerticalLayout {

  protected static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDForm.class);
  private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";

  private final Binder<CachedPatient> binder;

  private TextField externalIdField;
  private TextField patientIdField;
  private TextField patientFirstNameField;
  private TextField patientLastNameField;
  private TextField issuerOfPatientIdField;
  private Button addPatientButton;
  private Button clearFieldsButton;
  private transient InputStream inputStream;
  private Upload uploadCsvButton;
  private Div addedPatientLabelDiv;
  private Div uploadCsvLabelDiv;
  private ExternalIDGrid externalIDGrid;

  public ExternalIDForm(ExternalIDGrid externalIDGrid) {
    setSizeFull();
    this.externalIDGrid = externalIDGrid;

    binder = new BeanValidationBinder<>(CachedPatient.class);

    setElements();
    setBinder();

    clearFieldsButton.addClickListener(click -> clearPatientFields());

    addPatientButton.addClickListener(
        click -> {
          CachedPatient newPatient =
              new CachedPatient(
                  externalIdField.getValue(),
                  patientIdField.getValue(),
                  patientFirstNameField.getValue(),
                  patientLastNameField.getValue(),
                  issuerOfPatientIdField.getValue());
          binder.validate();
          if (binder.isValid()) {
            externalIDGrid.addPatient(newPatient);
            binder.readBean(null);
          }
        });

    // enable/disable update button while editing
    binder.addStatusChangeListener(
        event -> {
          boolean isValid = !event.hasValidationErrors();
          boolean hasChanges = binder.hasChanges();
          addPatientButton.setEnabled(hasChanges && isValid);
        });

    HorizontalLayout horizontalLayout1 = new HorizontalLayout();
    HorizontalLayout horizontalLayout2 = new HorizontalLayout();
    HorizontalLayout horizontalLayout3 = new HorizontalLayout();
    HorizontalLayout horizontalLayout4 = new HorizontalLayout();
    HorizontalLayout horizontalLayout5 = new HorizontalLayout();
    Div addPatientDiv = new Div();

    horizontalLayout1.setSizeFull();
    horizontalLayout2.setSizeFull();
    horizontalLayout3.setSizeFull();
    horizontalLayout4.setSizeFull();

    horizontalLayout1.add(uploadCsvLabelDiv);
    horizontalLayout2.add(uploadCsvButton);
    horizontalLayout3.add(addedPatientLabelDiv);

    horizontalLayout4.add(
        externalIdField,
        patientIdField,
        patientFirstNameField,
        patientLastNameField,
        issuerOfPatientIdField);
    horizontalLayout5.add(clearFieldsButton, addPatientButton);

    addPatientDiv.add(horizontalLayout4, horizontalLayout5);
    add(horizontalLayout1, horizontalLayout2, horizontalLayout3, addPatientDiv);
  }

  private void setElements() {
    setElementUploadCSV();

    uploadCsvLabelDiv = new Div();
    uploadCsvLabelDiv.setText(
        "Upload the CSV file containing the external ID associated with patient(s): ");
    uploadCsvLabelDiv.getStyle().set("font-size", "large").set("font-weight", "bolder");

    addedPatientLabelDiv = new Div();
    addedPatientLabelDiv = new Div();
    addedPatientLabelDiv.setText("Add a new patient: ");
    addedPatientLabelDiv.getStyle().set("font-size", "large").set("font-weight", "bolder");

    externalIdField = new TextField("External Pseudonym");
    externalIdField.setWidth("20");
    patientIdField = new TextField("Patient ID");
    patientIdField.setWidth("20%");
    patientFirstNameField = new TextField("Patient first name");
    patientFirstNameField.setWidth("20%");
    patientLastNameField = new TextField("Patient last name");
    patientLastNameField.setWidth("20%");
    issuerOfPatientIdField = new TextField("Issuer of patient ID");
    issuerOfPatientIdField.setWidth("20%");

    clearFieldsButton = new Button("Clear");

    addPatientButton = new Button("Add patient");
    addPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addPatientButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
  }

  public void setElementUploadCSV() {
    MemoryBuffer memoryBuffer = new MemoryBuffer();
    uploadCsvButton = new Upload(memoryBuffer);
    uploadCsvButton.setDropLabel(new Span("Drag and drop your CSV file here"));
    uploadCsvButton.addSucceededListener(
        event -> {
          inputStream = memoryBuffer.getInputStream();

          Dialog chooseSeparatorDialog = new Dialog();
          TextField separatorCSVField =
              new TextField("Choose the separator for reading the CSV file");
          separatorCSVField.setWidthFull();
          separatorCSVField.setMaxLength(1);
          separatorCSVField.setValue(",");
          Button openCSVButton = new Button("Open CSV");

          openCSVButton.addClickListener(
              buttonClickEvent -> {
                chooseSeparatorDialog.close();
                char separator = ',';
                if (!separatorCSVField.getValue().equals("")) {
                  separator = separatorCSVField.getValue().charAt(0);
                }
                CSVDialog csvDialog = new CSVDialog(inputStream, separator);
                csvDialog.open();

                csvDialog
                    .getReadCSVButton()
                    .addClickListener(
                        buttonClickEvent1 -> {
                          externalIDGrid.addPatientList(csvDialog.getPatientsList());
                          csvDialog.resetPatientsList();
                        });
              });

          chooseSeparatorDialog.add(separatorCSVField, openCSVButton);
          chooseSeparatorDialog.open();
          separatorCSVField.focus();
        });
  }

  public void setBinder() {
    binder
        .forField(externalIdField)
        .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("pseudonym");

    binder
        .forField(patientIdField)
        .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientId");

    binder
        .forField(patientFirstNameField)
        .withValidator(StringUtils::isNotBlank, "Patient first name is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientFirstName");

    binder
        .forField(patientLastNameField)
        .withValidator(StringUtils::isNotBlank, "Patient last name is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .bind("patientLastName");

    binder
        .forField(issuerOfPatientIdField)
        .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
        .bind("issuerOfPatientId");
  }

  public void clearPatientFields() {
    externalIdField.clear();
    patientIdField.clear();
    patientFirstNameField.clear();
    patientLastNameField.clear();
    issuerOfPatientIdField.clear();
    binder.readBean(null);
  }

  public Upload getUploadCsvButton() {
    return uploadCsvButton;
  }
}
