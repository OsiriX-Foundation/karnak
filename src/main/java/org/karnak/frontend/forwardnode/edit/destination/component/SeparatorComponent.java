/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SeparatorComponent extends VerticalLayout {

  HorizontalLayout separator;

  public SeparatorComponent() {

    // In order to not have a padding around the component
    setPadding(false);

    // Build TranscodeOnlyUncompressed components
    buildComponents();

    // Add components
    addComponents();
  }

  /** Build components used in separator */
  private void buildComponents() {
    separator = new HorizontalLayout();
    separator.getElement().getStyle().set("border-top", "dashed");
    separator.getElement().getStyle().set("width", "19%");
    separator.getElement().getStyle().set("border-top-color", "#e8ebef");
    separator.getElement().getStyle().set("margin-left", "0%");
    separator.getElement().getStyle().set("margin-top", "2%");
  }

  /** Add components in TranscodeOnlyUncompressed */
  private void addComponents() {
    add(separator);
  }
}
