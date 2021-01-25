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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.SourceNodeService;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class SourceNodesView extends VerticalLayout {

  private final SourceNodeService sourceNodeService;

  private final HorizontalLayout layoutFilterButton;
  private final TextField filter;
  private final Button newSourceNode;
  private final GridSourceNode gridSourceNode;

  private final String LABEL_NEW_SOURCE_NODE = "Source";
  private final String PLACEHOLDER_FILTER = "Filter properties of sources";

  @Autowired
  public SourceNodesView(SourceNodeService sourceNodeService) {
    this.sourceNodeService = sourceNodeService;
    setSizeFull();
    this.gridSourceNode = new GridSourceNode();
    filter = new TextField();
    newSourceNode = new Button(LABEL_NEW_SOURCE_NODE);
    layoutFilterButton = new HorizontalLayout(filter, newSourceNode);
    layoutFilterButton.setVerticalComponentAlignment(Alignment.START, filter);
    layoutFilterButton.expand(filter);

    setTextFieldFilter();
    setButtonNewDestinationDICOM();

    add(UIS.setWidthFull(layoutFilterButton), UIS.setWidthFull(gridSourceNode));
  }

  private void setTextFieldFilter() {
    filter.setPlaceholder(PLACEHOLDER_FILTER);
    // Apply the filter to grid's data provider. TextField value is never null
    filter.addValueChangeListener(event -> sourceNodeService.setFilter(event.getValue()));
  }

  private void setButtonNewDestinationDICOM() {
    newSourceNode.getElement().setAttribute("title", "New destination of type dicom");
    newSourceNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    newSourceNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
  }

  protected void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
    setEnabled(forwardNodeEntity != null);
    sourceNodeService.setForwardNode(forwardNodeEntity);
    gridSourceNode.setDataProvider(sourceNodeService);
  }

  public Button getNewSourceNode() {
    return newSourceNode;
  }

  public GridSourceNode getGridSourceNode() {
    return gridSourceNode;
  }
}
