/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.mainzelliste;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.frontend.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = MainzellisteView.ROUTE, layout = MainLayout.class)
@PageTitle("KARNAK - Mainzelliste")
@Tag("mainzelliste-view")
@Secured({"ROLE_admin"})
@SuppressWarnings("serial")
public class MainzellisteView extends HorizontalLayout {

  public static final String VIEW_NAME = "Mainzelliste pseudonym";
  public static final String ROUTE = "mainzelliste";

  public MainzellisteView() {
    setSizeFull();
    VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.add(new H2("Mainzelliste Pseudonym"), new MainzellisteAddPatient());
    add(verticalLayout);
  }
}
