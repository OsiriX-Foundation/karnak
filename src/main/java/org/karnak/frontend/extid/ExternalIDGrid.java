/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.backend.cache.PatientClient;
import org.karnak.backend.cache.PseudonymPatient;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.util.PatientClientUtil;
import org.vaadin.klaudeta.PaginatedGrid;

public class ExternalIDGrid extends PaginatedGrid<CachedPatient> {

  private static final String ERROR_MESSAGE_PATIENT = "Length must be between 1 and 50.";
  private static final String LABEL_SAVE = "Save";
  private static final String LABEL_CANCEL = "Cancel";
  private final Binder<CachedPatient> binder;
  private final List<CachedPatient> patientList;
  private final transient PatientClient externalIDCache;
  private Button addPatientButton;
  private Button deletePatientButton;
  private Button saveEditPatientButton;
  private Button cancelEditPatientButton;
  private Editor<CachedPatient> editor;
  private Collection<Button> editButtons;
  private TextField externalIdField;
  private TextField patientIdField;
  private TextField patientFirstNameField;
  private TextField patientLastNameField;
  private TextField issuerOfPatientIdField;
  private Grid.Column<CachedPatient> deleteColumn;

  public ExternalIDGrid() {
    binder = new Binder<>(CachedPatient.class);
    patientList = new ArrayList<>();
    externalIDCache = AppConfig.getInstance().getExternalIDCache();
    setPageSize(4);
    setPaginatorSize(2);

    setSizeFull();
    getElement()
        .addEventListener("keyup", event -> editor.cancel())
        .setFilter("event.key === 'Escape' || event.key === 'Esc'");
    setHeightByRows(true);
    setItems(patientList);
    setElements();
    setBinder();
    readAllCacheValue();
    editor.addOpenListener(
        e -> {
          editButtons.stream().forEach(button -> button.setEnabled(!editor.isOpen()));
          deleteColumn.setVisible(false);
          addPatientButton.setVisible(false);
        });

    editor.addCloseListener(
        e -> {
          editButtons.stream().forEach(button -> button.setEnabled(!editor.isOpen()));
          deleteColumn.setVisible(true);
          addPatientButton.setVisible(true);
        });

    saveEditPatientButton.addClickListener(
        e -> {
          final CachedPatient patientEdit =
              new CachedPatient(
                  externalIdField.getValue(),
                  patientIdField.getValue(),
                  patientFirstNameField.getValue(),
                  patientLastNameField.getValue(),
                  issuerOfPatientIdField.getValue());
          externalIDCache.remove(PatientClientUtil.generateKey(editor.getItem())); // old extid
          externalIDCache.put(PatientClientUtil.generateKey(patientEdit), patientEdit); // new extid
          editor.save();
        });
    saveEditPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    cancelEditPatientButton.addClickListener(e -> editor.cancel());
  }

  private void setElements() {
    Grid.Column<CachedPatient> extidColumn =
        addColumn(CachedPatient::getPseudonym).setHeader("External Pseudonym").setSortable(true);
    Grid.Column<CachedPatient> patientIdColumn =
        addColumn(CachedPatient::getPatientId).setHeader("Patient ID").setSortable(true);
    Grid.Column<CachedPatient> patientFirstNameColumn =
        addColumn(CachedPatient::getPatientFirstName)
            .setHeader("Patient first name")
            .setSortable(true);
    Grid.Column<CachedPatient> patientLastNameColumn =
        addColumn(CachedPatient::getPatientLastName)
            .setHeader("Patient last name")
            .setSortable(true);
    Grid.Column<CachedPatient> issuerOfPatientIDColumn =
        addColumn(CachedPatient::getIssuerOfPatientId)
            .setHeader("Issuer of patient ID")
            .setSortable(true);
    Grid.Column<CachedPatient> editorColumn =
        addComponentColumn(
            patient -> {
              Button edit = new Button("Edit");
              edit.addClassName("edit");
              edit.addClickListener(
                  e -> {
                    editor.editItem(patient);
                    externalIdField.focus();
                  });
              edit.setEnabled(!editor.isOpen());
              editButtons.add(edit);
              return edit;
            });

    editButtons = Collections.newSetFromMap(new WeakHashMap<>());
    editor = getEditor();
    editor.setBinder(binder);
    editor.setBuffered(true);

    externalIdField = new TextField();
    patientIdField = new TextField();
    patientFirstNameField = new TextField();
    patientLastNameField = new TextField();
    issuerOfPatientIdField = new TextField();

    extidColumn.setEditorComponent(externalIdField);
    patientIdColumn.setEditorComponent(patientIdField);
    patientFirstNameColumn.setEditorComponent(patientFirstNameField);
    patientLastNameColumn.setEditorComponent(patientLastNameField);
    issuerOfPatientIDColumn.setEditorComponent(issuerOfPatientIdField);

    deleteColumn =
        addComponentColumn(
            patient -> {
              deletePatientButton = new Button("Delete");
              deletePatientButton.addThemeVariants(
                  ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
              deletePatientButton.addClickListener(
                  e -> {
                    patientList.remove(patient);
                    getDataProvider().refreshAll();
                    externalIDCache.remove(PatientClientUtil.generateKey(patient));
                  });
              return deletePatientButton;
            });

    saveEditPatientButton = new Button(LABEL_SAVE);
    cancelEditPatientButton = new Button(LABEL_CANCEL);

    Div buttons = new Div(saveEditPatientButton, cancelEditPatientButton);
    editorColumn.setEditorComponent(buttons);
  }

  public Div setBinder() {
    Div validationStatus = new Div();
    validationStatus.setId("validation");
    validationStatus.getStyle().set("color", "var(--theme-color, red)");
    binder
        .forField(externalIdField)
        .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .withStatusLabel(validationStatus)
        .bind("pseudonym");

    binder
        .forField(patientIdField)
        .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
        .withValidator(new StringLengthValidator(ERROR_MESSAGE_PATIENT, 1, 50))
        .withStatusLabel(validationStatus)
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
        .withStatusLabel(validationStatus)
        .bind("issuerOfPatientId");

    return validationStatus;
  }

  public void readAllCacheValue() {
    if (externalIDCache != null) {
      Collection<PseudonymPatient> pseudonymPatients = externalIDCache.getAll();
      Collection<CachedPatient> cachedPatients = new ArrayList<>();
      for (Iterator<PseudonymPatient> iterator = pseudonymPatients.iterator();
          iterator.hasNext(); ) {
        final CachedPatient patient = (CachedPatient) iterator.next();
        cachedPatients.add(patient);
      }
      setItems(cachedPatients);
    }
    refreshPaginator();
  }

  public void addPatient(CachedPatient newPatient) {
    if (patientExist(newPatient)) {
      WarningDialog warningDialog =
          new WarningDialog(
              "Duplicate data",
              String.format(
                  "You are trying to insert two equivalent pseudonyms or identical patients: {%s}",
                  newPatient.toString()),
              "ok");
      warningDialog.open();
    } else {
      externalIDCache.put(PatientClientUtil.generateKey(newPatient), newPatient);
      readAllCacheValue();
    }
  }

  public void addPatientList(List<CachedPatient> patientList) {
    patientList.forEach(this::addPatient);
    readAllCacheValue();
  }

  public boolean patientExist(PseudonymPatient patient) {
    ListDataProvider<CachedPatient> dataProvider =
        (ListDataProvider<CachedPatient>) getDataProvider();
    for (PseudonymPatient patientElem : dataProvider.getItems()) {
      if (patientElem.getPseudonym().equals(patient.getPseudonym())
          || (patientElem.getPatientId().equals(patient.getPatientId())
              && patientElem.getIssuerOfPatientId().equals(patient.getIssuerOfPatientId()))) {
        return true;
      }
    }
    return false;
  }
}
