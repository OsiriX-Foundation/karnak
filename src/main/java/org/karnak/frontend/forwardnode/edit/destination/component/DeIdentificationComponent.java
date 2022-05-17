/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import static org.karnak.backend.enums.PseudonymType.CACHE_EXTID;
import static org.karnak.backend.enums.PseudonymType.EXTID_IN_TAG;
import static org.karnak.backend.enums.PseudonymType.MAINZELLISTE_EXTID;
import static org.karnak.backend.enums.PseudonymType.MAINZELLISTE_PID;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.io.Serial;
import java.util.Objects;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.component.ProjectDropDown;
import org.karnak.frontend.util.UIS;

public class DeIdentificationComponent extends VerticalLayout {

  @Serial
  private static final long serialVersionUID = -4535591077096019645L;

  // Labels
  private static final String LABEL_CHECKBOX_DEIDENTIFICATION = "Activate de-identification";

  private static final String LABEL_DISCLAIMER_DEIDENTIFICATION =
      "In order to ensure complete de-identification, visual verification of metadata and images is necessary.";

  private static final String LABEL_DEFAULT_ISSUER =
      "If this field is empty, the Issuer of Patient ID is not used to define the authenticity of the patient";

  // Components
  private Checkbox deIdentificationCheckbox;

  private Label disclaimerLabel;

  private ProjectDropDown projectDropDown;

  private PseudonymInDicomTagComponent pseudonymInDicomTagComponent;

  private Binder<DestinationEntity> destinationBinder;

  private Div pseudonymDicomTagDiv;

  private Div deIdentificationDiv;

  private ProfileLabel profileLabel;

  private WarningNoProjectsDefined warningNoProjectsDefined;

  private Select<String> pseudonymTypeSelect;

  private TextField issuerOfPatientIDByDefault;

  private final DestinationComponentUtil destinationComponentUtil;

  /** Constructor */
  public DeIdentificationComponent() {
    this.destinationComponentUtil = new DestinationComponentUtil();
  }

  /**
   * Init deidentification component
   *
   * @param binder Binder for checks
   */
  public void init(final Binder<DestinationEntity> binder) {
    // Init destination binder
    setDestinationBinder(binder);

    // Build deidentification components
    buildComponents();

    // Init destination binder
    initDestinationBinder();

    // Build Listeners
    buildListeners();

    // Add components
    addComponents();
  }

  /** Add components */
  private void addComponents() {
    // Padding
    setPadding(true);

    // Add components in deidentification div
    deIdentificationDiv.add(
        disclaimerLabel,
        projectDropDown,
        profileLabel,
        pseudonymTypeSelect,
        pseudonymDicomTagDiv,
        issuerOfPatientIDByDefault);

    // If checkbox is checked set div visible, invisible otherwise
    deIdentificationDiv.setVisible(deIdentificationCheckbox.getValue());

    // Add components in view
    add(UIS.setWidthFull(new HorizontalLayout(deIdentificationCheckbox, deIdentificationDiv)));
  }

  /** Build listeners */
  private void buildListeners() {
    buildPseudonymTypeListener();
    destinationComponentUtil.buildWarningNoProjectDefinedListener(warningNoProjectsDefined, deIdentificationCheckbox);
    destinationComponentUtil.buildProjectDropDownListener(projectDropDown, profileLabel);
  }

  /** Build deidentification components */
  private void buildComponents() {
    buildIssuerOfPatientID();
    projectDropDown = destinationComponentUtil.buildProjectDropDown();
    profileLabel = new ProfileLabel();
    warningNoProjectsDefined = destinationComponentUtil.buildWarningNoProjectDefined();
    deIdentificationCheckbox = destinationComponentUtil.buildActivateCheckbox(LABEL_CHECKBOX_DEIDENTIFICATION);
    buildDisclaimerLabel();
    buildPseudonymTypeSelect();
    buildPseudonymInDicomTagComponent();
    deIdentificationDiv = destinationComponentUtil.buildActivateDiv();
    buildPseudonymDicomTagDiv();
  }

  /** Build Pseudonym In Dicom Tag Component */
  private void buildPseudonymInDicomTagComponent() {
    pseudonymInDicomTagComponent = new PseudonymInDicomTagComponent(destinationBinder);
  }

  /** Build Pseudonym Dicom Tag Div which is visible if "Pseudonym is in a dicom tag" is selected */
  private void buildPseudonymDicomTagDiv() {
    pseudonymDicomTagDiv = new Div();
    pseudonymDicomTagDiv.add(pseudonymInDicomTagComponent);
  }

  /** Build pseudonym type */
  private void buildPseudonymTypeSelect() {
    pseudonymTypeSelect = new Select<>();
    pseudonymTypeSelect.setLabel("Pseudonym type");
    pseudonymTypeSelect.setWidth("100%");
    pseudonymTypeSelect.getStyle().set("right", "0px");
    pseudonymTypeSelect.setItems(
        MAINZELLISTE_PID.getValue(),
        MAINZELLISTE_EXTID.getValue(),
        CACHE_EXTID.getValue(),
        EXTID_IN_TAG.getValue());
  }

  /** Build disclaimer */
  private void buildDisclaimerLabel() {
    disclaimerLabel = new Label(LABEL_DISCLAIMER_DEIDENTIFICATION);
    disclaimerLabel.getStyle().set("color", "red");
    disclaimerLabel.setMinWidth("75%");
    disclaimerLabel.getStyle().set("right", "0px");
  }

  /** Build issuer of patient ID */
  private void buildIssuerOfPatientID() {
    issuerOfPatientIDByDefault = new TextField();
    issuerOfPatientIDByDefault.setLabel("Issuer of Patient ID by default");
    issuerOfPatientIDByDefault.setWidth("100%");
    issuerOfPatientIDByDefault.setPlaceholder(LABEL_DEFAULT_ISSUER);
    UIS.setTooltip(issuerOfPatientIDByDefault, LABEL_DEFAULT_ISSUER);
  }



  /** Listener on pseudonym type */
  private void buildPseudonymTypeListener() {
    pseudonymTypeSelect.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            pseudonymDicomTagDiv.setVisible(
                Objects.equals(event.getValue(), EXTID_IN_TAG.getValue()));
          }
        });
  }

  private void initDestinationBinder() {
    destinationBinder
        .forField(issuerOfPatientIDByDefault)
        .bind(
            DestinationEntity::getIssuerByDefault,
            (destinationEntity, s) -> {
              if (deIdentificationCheckbox.getValue()) {
                destinationEntity.setIssuerByDefault(s);
              } else {
                destinationEntity.setIssuerByDefault("");
              }
            });
    destinationBinder
        .forField(deIdentificationCheckbox)
        .bind(DestinationEntity::isDesidentification, DestinationEntity::setDesidentification);
    destinationBinder
        .forField(projectDropDown)
        .withValidator(
            project -> project != null || !deIdentificationCheckbox.getValue(),
            "Choose a project")
        .bind(DestinationEntity::getProjectEntity, DestinationEntity::setProjectEntity);

    destinationBinder
        .forField(pseudonymTypeSelect)
        .withValidator(Objects::nonNull, "Choose pseudonym type\n")
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
  }

  /**
   * Clean fields of destination which are not saved because not selected by user
   *
   * @param destinationEntity Destination to clean
   */
  public void cleanUnSavedData(DestinationEntity destinationEntity) {
    // Reset the destination for the part tag is in dicom tag in case the pseudonym
    // type selected is
    // not pseudonym in dicom tag or deidentification not active
    if (!destinationEntity.isDesidentification()
        || !Objects.equals(destinationEntity.getPseudonymType(), EXTID_IN_TAG)) {
      destinationEntity.setTag(null);
      destinationEntity.setDelimiter(null);
      destinationEntity.setPosition(null);
      destinationEntity.setSavePseudonym(null);
    }

    if (!destinationEntity.isDesidentification()) {
      // Reset the destination for pseudonym type, project, issuer of patient id
      destinationEntity.setProjectEntity(null);
      destinationEntity.setPseudonymType(MAINZELLISTE_PID);
      destinationEntity.setIssuerByDefault(null);
    }
  }

  public Binder<DestinationEntity> getDestinationBinder() {
    return destinationBinder;
  }

  public void setDestinationBinder(Binder<DestinationEntity> destinationBinder) {
    this.destinationBinder = destinationBinder;
  }

  public ProjectDropDown getProjectDropDown() {
    return projectDropDown;
  }

  public Checkbox getDeIdentificationCheckbox() {
    return deIdentificationCheckbox;
  }

  public Label getDisclaimerLabel() {
    return disclaimerLabel;
  }

  public PseudonymInDicomTagComponent getPseudonymInDicomTagComponent() {
    return pseudonymInDicomTagComponent;
  }

  public Div getPseudonymDicomTagDiv() {
    return pseudonymDicomTagDiv;
  }

  public Div getDeIdentificationDiv() {
    return deIdentificationDiv;
  }

  public WarningNoProjectsDefined getWarningNoProjectsDefined() {
    return warningNoProjectsDefined;
  }

  public Select<String> getPseudonymTypeSelect() {
    return pseudonymTypeSelect;
  }

  public TextField getIssuerOfPatientIDByDefault() {
    return issuerOfPatientIDByDefault;
  }

  public DestinationComponentUtil getDestinationComponentUtil() {
    return destinationComponentUtil;
  }

  public ProfileLabel getProfileLabel() {
    return profileLabel;
  }
}
