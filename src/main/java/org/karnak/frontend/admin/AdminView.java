/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.admin;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;

/** Admin view that is registered dynamically on admin user login. */
@PageTitle("KARNAK - Admin")
@SuppressWarnings("serial")
public class AdminView extends VerticalLayout {

  public static final String VIEW_NAME = "Admin";

  public AdminView() {
    add(new H2("You are connected as an admin."));
  }
}
