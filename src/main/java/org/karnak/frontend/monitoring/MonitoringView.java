/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.monitoring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.frontend.MainLayout;
import org.karnak.frontend.monitoring.component.TransferStatusGrid;
import org.karnak.frontend.util.UIS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/** Monitoring View */
@Route(value = MonitoringView.ROUTE, layout = MainLayout.class)
@PageTitle("KARNAK - Monitoring")
@Secured({"ROLE_admin"})
public class MonitoringView extends VerticalLayout {

  public static final String VIEW_NAME = "Monitoring";
  public static final String ROUTE = "monitoring";

  // Monitoring Logic
  private final MonitoringLogic monitoringLogic;

  // UI components
  private TransferStatusGrid transferStatusGrid;
  private final TransferStatusDataProvider<TransferStatusEntity> transferStatusDataProvider;
  private Button refreshGridButton;

  /**
   * Autowired constructor.
   *
   * @param monitoringLogic Monitoring Logic used to call backend services and implement logic
   *     linked to the monitoring view
   */
  @Autowired
  public MonitoringView(
      final MonitoringLogic monitoringLogic,
      final TransferStatusDataProvider<TransferStatusEntity> transferStatusDataProvider) {
    // Bind the autowired service
    this.monitoringLogic = monitoringLogic;
    this.transferStatusDataProvider = transferStatusDataProvider;

    // Set the view in the service
    this.monitoringLogic.setMonitoringView(this);

    // Build  components
    buildComponents();

    // Add components in the view
    addComponentsView();
  }

  /** Build components */
  private void buildComponents() {
    // Paginated Grid + data provider
    transferStatusGrid = new TransferStatusGrid(transferStatusDataProvider);
    transferStatusDataProvider.setFilter(transferStatusGrid.getTransferStatusFilter());
    transferStatusGrid.setDataProvider(transferStatusDataProvider);

    // Refresh button
    refreshGridButton = new Button("Refresh");
    refreshGridButton.addClickListener(buttonClickEvent -> transferStatusDataProvider.refreshAll());
  }

  /** Add components in the view */
  private void addComponentsView() {
    add(transferStatusGrid);
    add(UIS.setWidthFull(refreshGridButton));
    setSizeFull();
    setWidthFull();
  }
}
