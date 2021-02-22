/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode;

import static org.karnak.backend.enums.PseudonymType.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectDataProvider;
import org.karnak.frontend.project.MainViewProjects;
import org.karnak.frontend.util.UIS;

public class LayoutDesidentification extends Div {

  private final Binder<DestinationEntity> destinationBinder;
  private final ProjectDropDown projectDropDown;
  private final DesidentificationName desidentificationName;
  private final ProjectDataProvider projectDataProvider;
  private final WarningNoProjectsDefined warningNoProjectsDefined;
  private static final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";
  private static final String LABEL_DISCLAIMER_DEIDENTIFICATION =
      "In order to ensure complete de-identification, visual verification of metadata and images is necessary.";
  private Checkbox checkboxDesidentification;
  private Label labelDisclaimer;
  private Checkbox checkboxUseAsPatientName;
  private ExtidPresentInDicomTagView extidPresentInDicomTagView;
  private Div div;
  private Select<String> extidListBox;

  public LayoutDesidentification(Binder<DestinationEntity> destinationBinder) {
    projectDataProvider = new ProjectDataProvider();
    this.destinationBinder = destinationBinder;
    projectDropDown = new ProjectDropDown();
    desidentificationName = new DesidentificationName();

    warningNoProjectsDefined = new WarningNoProjectsDefined();
    warningNoProjectsDefined.setTextBtnCancel("Continue");
    warningNoProjectsDefined.setTextBtnValidate("Create a project");

    setElements();
    setBinder();
    setEventCheckboxDesidentification();
    setEventExtidListBox();
    setEventWarningDICOM();

    add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, div)));

    if (checkboxDesidentification.getValue()) {
      div.add(labelDisclaimer, projectDropDown, desidentificationName, extidListBox);
    }

    projectDropDown.addValueChangeListener(event -> setTextOnSelectionProject(event.getValue()));
  }

  private void setElements() {
    checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
    checkboxDesidentification.setValue(true);
    checkboxDesidentification.setMinWidth("25%");

    labelDisclaimer = new Label(LABEL_DISCLAIMER_DEIDENTIFICATION);
    labelDisclaimer.getStyle().set("color", "red");
    labelDisclaimer.setMinWidth("75%");
    labelDisclaimer.getStyle().set("right", "0px");

    projectDropDown.setLabel("Choose a project");
    projectDropDown.setWidth("100%");

    extidListBox = new Select<>();
    extidListBox.setLabel("Pseudonym type");
    extidListBox.setWidth("100%");
    extidListBox.getStyle().set("right", "0px");
    extidListBox.setItems(
        MAINZELLISTE_PID.getValue(),
        MAINZELLISTE_EXTID.getValue(),
        CACHE_EXTID.getValue(),
        EXTID_IN_TAG.getValue());

    checkboxUseAsPatientName = new Checkbox("Uses the pseudonym as Patient Name");

    extidPresentInDicomTagView = new ExtidPresentInDicomTagView(destinationBinder);
    div = new Div();
    div.setWidth("100%");
  }

  private void setEventWarningDICOM() {
    warningNoProjectsDefined
        .getBtnCancel()
        .addClickListener(
            btnEvent -> {
              checkboxDesidentification.setValue(false);
              warningNoProjectsDefined.close();
            });
    warningNoProjectsDefined
        .getBtnValidate()
        .addClickListener(
            btnEvent -> {
              warningNoProjectsDefined.close();
              navigateToProject();
            });
  }

  private void navigateToProject() {
    getUI().ifPresent(nav -> nav.navigate(MainViewProjects.VIEW_NAME.toLowerCase()));
  }

  private void setEventCheckboxDesidentification() {
    checkboxDesidentification.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            if (event.getValue()) {
              if (projectDataProvider.getAllProjects().size() > 0) {
                div.add(labelDisclaimer, projectDropDown, desidentificationName, extidListBox);
                setTextOnSelectionProject(projectDropDown.getValue());
              } else {
                warningNoProjectsDefined.open();
              }
            } else {
              div.remove(labelDisclaimer, projectDropDown, desidentificationName);
              extidListBox.setValue(MAINZELLISTE_PID.getValue());
              checkboxUseAsPatientName.clear();
              extidPresentInDicomTagView.clear();
              div.remove(extidListBox);
              div.remove(checkboxUseAsPatientName);
              div.remove(extidPresentInDicomTagView);
            }
          }
        });
  }

  private void setTextOnSelectionProject(ProjectEntity projectEntity) {
    if (projectEntity != null && projectEntity.getProfileEntity() != null) {
      desidentificationName.setShowValue(
          String.format("The profile %s will be used", projectEntity.getProfileEntity().getName()));
    } else if (projectEntity != null && projectEntity.getProfileEntity() == null) {
      desidentificationName.setShowValue("No profiles defined in the project");
    } else {
      desidentificationName.removeAll();
    }
  }

  private void setEventExtidListBox() {
    extidListBox.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            if (event.getValue().equals(MAINZELLISTE_PID.getValue())) {
              checkboxUseAsPatientName.clear();
              extidPresentInDicomTagView.clear();
              div.remove(checkboxUseAsPatientName);
              div.remove(extidPresentInDicomTagView);
            } else if (event.getValue().equals(MAINZELLISTE_EXTID.getValue())
                || event.getValue().equals(CACHE_EXTID.getValue())) {
              div.add(UIS.setWidthFull(checkboxUseAsPatientName));
              extidPresentInDicomTagView.clear();
              div.remove(extidPresentInDicomTagView);
            } else {
              div.add(UIS.setWidthFull(checkboxUseAsPatientName));
              extidPresentInDicomTagView.enableComponent();
              div.add(extidPresentInDicomTagView);
            }
          }
        });
  }

  private void setBinder() {
    destinationBinder
        .forField(checkboxDesidentification)
        .bind(DestinationEntity::getDesidentification, DestinationEntity::setDesidentification);
    destinationBinder
        .forField(projectDropDown)
        .withValidator(
            project ->
                project != null || (project == null && !checkboxDesidentification.getValue()),
            "Choose a project")
        .bind(DestinationEntity::getProjectEntity, DestinationEntity::setProjectEntity);

    destinationBinder
        .forField(extidListBox)
        .withValidator(type -> type != null, "Choose pseudonym type\n")
        .bind(
            destination -> {
              if (destination.getPseudonymType().equals(MAINZELLISTE_PID)) {
                return MAINZELLISTE_PID.getValue();
              } else if (destination.getPseudonymType().equals(MAINZELLISTE_EXTID)) {
                return MAINZELLISTE_EXTID.getValue();
              } else if (destination.getPseudonymType().equals(CACHE_EXTID)) {
                return CACHE_EXTID.getValue();
              } else {
                return EXTID_IN_TAG.getValue();
              }
            },
            (destination, s) -> {
              if (s.equals(MAINZELLISTE_PID.getValue())) {
                destination.setPseudonymType(MAINZELLISTE_PID);
              } else if (s.equals(MAINZELLISTE_EXTID.getValue())) {
                destination.setPseudonymType(MAINZELLISTE_EXTID);
              } else if (s.equals(CACHE_EXTID.getValue())) {
                destination.setPseudonymType(CACHE_EXTID);
              } else {
                destination.setPseudonymType(EXTID_IN_TAG);
              }
            });

    destinationBinder
        .forField(checkboxUseAsPatientName)
        .bind(
            DestinationEntity::getPseudonymAsPatientName,
            DestinationEntity::setPseudonymAsPatientName);
  }
}
