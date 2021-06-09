/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.help;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.frontend.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("KARNAK - Help")
@Tag("help-view")
@Secured({"ADMIN"})
public class HelpView extends VerticalLayout {

  public static final String VIEW_NAME = "Help";

  public HelpView() {
    setSizeFull();
    H1 heading = new H1("Help");

    Anchor generalDoc =
        new Anchor(
            "https://osirix-foundation.github.io/karnak-documentation/", "General documentation");
    generalDoc.setTarget("_blank");

    Anchor installation =
        new Anchor(
            "https://osirix-foundation.github.io/karnak-documentation/docs/installation",
            "Installation and configuration with Docker");
    installation.setTarget("_blank");

    Anchor profile =
        new Anchor(
            "https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/profiles",
            "Build your own profile for de-identification or for tag morphing");
    profile.setTarget("_blank");
    VerticalLayout layout = new VerticalLayout();
    layout.add(heading, generalDoc, installation, profile);
    this.add(layout);
  }
}
