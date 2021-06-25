/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.pseudonym.mapping;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.cache.MainzellistePatient;
import org.karnak.frontend.MainLayout;
import org.karnak.frontend.pseudonym.mapping.component.MappingMainzellisteComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = "mapping", layout = MainLayout.class)
@PageTitle("KARNAK - Mainzelliste Mapping Pseudonym")
@Tag("mainzelliste-mapping-pseudonym-view")
@Secured({"ROLE_investigator"})
@SuppressWarnings("serial")
public class PseudonymMappingView extends HorizontalLayout {

  public static final String VIEW_NAME = "Pseudonym mapping ";

  // Mapping Logic
  private final PseudonymMappingLogic pseudonymMappingLogic;

  // Components
  private MappingMainzellisteComponent mappingMainzellisteComponent;

  /**
   * Autowired constructor.
   *
   * @param pseudonymMappingLogic Mapping Logic used to call backend services and implement logic
   *     linked to the view
   */
  @Autowired
  public PseudonymMappingView(final PseudonymMappingLogic pseudonymMappingLogic) {
    // Bind the autowired service
    this.pseudonymMappingLogic = pseudonymMappingLogic;

    // Set the view in the service
    this.pseudonymMappingLogic.setMappingView(this);

    // Build  components
    buildComponents();

    // Build listeners
    buildListeners();

    // Add components in the view
    addComponentsView();
  }

  /** Build listeners */
  private void buildListeners() {

    // Find patient listener
    mappingMainzellisteComponent
        .getFindButton()
        .addClickListener(
            event -> {
              if (mappingMainzellisteComponent.getPseudonymTextField().getValue() != null
                  && !mappingMainzellisteComponent.getPseudonymTextField().getValue().isEmpty()) {

                // Retrieve patient
                MainzellistePatient mainzellistePatientFound =
                    pseudonymMappingLogic.retrieveMainzellistePatient(
                        mappingMainzellisteComponent.getPseudonymTextField().getValue());

                // handle result find patient
                mappingMainzellisteComponent.handleResultFindPatient(mainzellistePatientFound);
              }
            });
  }

  /** Build components */
  private void buildComponents() {
    mappingMainzellisteComponent = new MappingMainzellisteComponent();
    mappingMainzellisteComponent.getResultDiv().setVisible(false);
  }

  /** Add components in the view */
  private void addComponentsView() {
    setSizeFull();
    VerticalLayout mappingLayout = new VerticalLayout();

    // Titles
    mappingLayout.add(new H2("Pseudonym Mapping"));

    // Mainzelliste
    mappingLayout.add(mappingMainzellisteComponent);

    // Layout
    add(mappingLayout);
  }
}
