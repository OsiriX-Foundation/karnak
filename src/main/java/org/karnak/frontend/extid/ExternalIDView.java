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

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.frontend.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class ExternalIDView extends HorizontalLayout {

  public static final String VIEW_NAME = "External pseudonym";
  private static final String LABEL_DISCLAIMER_EXTID =
      "WARNING: The data that is added to this grid will be stored"
          + " temporally for a short period of time. If the machine restarts, the data will be deleted.";
  private final ListDataProvider<CachedPatient> dataProvider;
  private final ExternalIDGrid externalIDGrid;
  private final Div validationStatus;
  private final ExternalIDForm externalIDForm;

  // https://vaadin.com/components/vaadin-grid/java-examples/assigning-data
  public ExternalIDView() {
    setSizeFull();
    VerticalLayout verticalLayout = new VerticalLayout();

    Label labelDisclaimer = new Label(LABEL_DISCLAIMER_EXTID);
    labelDisclaimer.getStyle().set("color", "red");
    labelDisclaimer.setMinWidth("75%");
    labelDisclaimer.getStyle().set("right", "0px");

    externalIDGrid = new ExternalIDGrid();
    dataProvider = (ListDataProvider<CachedPatient>) externalIDGrid.getDataProvider();
    externalIDForm = new ExternalIDForm(dataProvider);
    externalIDGrid.setAddPatientButton(externalIDForm.getAddPatientButton());

    validationStatus = externalIDGrid.setBinder();

    verticalLayout.add(
        new H2("External Pseudonym"),
        labelDisclaimer,
        externalIDForm,
        validationStatus,
        externalIDGrid);

    add(verticalLayout);
  }
}
