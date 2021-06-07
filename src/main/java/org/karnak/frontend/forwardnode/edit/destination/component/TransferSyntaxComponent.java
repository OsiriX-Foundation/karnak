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

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.dcm4che3.data.UID;
import org.karnak.backend.data.entity.DestinationEntity;

/** Create a transfer syntax component */
public class TransferSyntaxComponent extends VerticalLayout {

  private ComboBox<String> transferSyntaxComboBox;

  public TransferSyntaxComponent() {

    // In order to not have a padding around the component
    setPadding(false);

    // Build transfer syntax components
    buildComponents();

    // Add components
    addComponents();
  }

  /** Add components in transfer syntax */
  private void addComponents() {
    add(transferSyntaxComboBox);
  }

  /** Build components used in Transfer Syntax component */
  private void buildComponents() {
    transferSyntaxComboBox = new ComboBox<>("Transfer Syntax");
    transferSyntaxComboBox.setClearButtonVisible(true);
    transferSyntaxComboBox.setWidth("700px");
    transferSyntaxComboBox.getElement().getStyle().set("--vaadin-combo-box-overlay-width", "650px");
    // Values
    transferSyntaxComboBox.setItems(
        Arrays.stream(UID.class.getFields())
            .map(field -> UID.forName(field.getName()))
            .collect(Collectors.toList()));
    // Labels
    transferSyntaxComboBox.setItemLabelGenerator(UID::nameOf);
  }

  public void init(Binder<DestinationEntity> binder) {
    binder
        .forField(transferSyntaxComboBox)
        .bind(DestinationEntity::getTransferSyntax, DestinationEntity::setTransferSyntax);
  }
}
