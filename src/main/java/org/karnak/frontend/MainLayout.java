/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.karnak.backend.util.SecurityUtil;
import org.karnak.frontend.dicom.DicomMainView;
import org.karnak.frontend.extid.ExternalIDView;
import org.karnak.frontend.forwardnode.ForwardNodeView;
import org.karnak.frontend.help.HelpView;
import org.karnak.frontend.mainzelliste.MainzellisteView;
import org.karnak.frontend.monitoring.MonitoringView;
import org.karnak.frontend.profile.ProfileView;
import org.karnak.frontend.project.ProjectView;
import org.karnak.frontend.pseudonym.mapping.PseudonymMappingView;
import org.springframework.security.access.annotation.Secured;

/**
 * The main layout. Contains the navigation menu.
 */
@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@JsModule("@polymer/iron-icons/iron-icons.js")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(value = "./styles/shared-styles.css")
@CssImport(value = "./styles/empty.css", include = "lumo-badge")
@Route(value = "mainLayout")
@Secured({"ROLE_admin"})
@SuppressWarnings("serial")
public class MainLayout extends FlexLayout implements RouterLayout {

  private final Menu menu;

  public MainLayout() {
    setSizeFull();
    setClassName("main-layout");

    menu = new Menu();

    // Add secured Menu
    addSecuredMenu(
        ForwardNodeView.class, ForwardNodeView.VIEW_NAME, new IronIcon("icons", "settings"));
    addSecuredMenu(ProfileView.class, ProfileView.VIEW_NAME, new IronIcon("icons", "assignment"));
    addSecuredMenu(ProjectView.class, ProjectView.VIEW_NAME, new IronIcon("icons", "class"));
    addSecuredMenu(
        ExternalIDView.class, ExternalIDView.VIEW_NAME, new IronIcon("icons", "perm-identity"));
    addSecuredMenu(
        MainzellisteView.class, MainzellisteView.VIEW_NAME, new IronIcon("icons", "perm-identity"));
    addSecuredMenu(
        PseudonymMappingView.class,
        PseudonymMappingView.VIEW_NAME,
        new IronIcon("icons", "perm-identity"));
    addSecuredMenu(
        MonitoringView.class, MonitoringView.VIEW_NAME, new IronIcon("icons", "history"));
    addSecuredMenu(DicomMainView.class, DicomMainView.VIEW_NAME, new IronIcon("icons", "build"));
    addSecuredMenu(HelpView.class, HelpView.VIEW_NAME, new IronIcon("icons", "help"));
    // menu.addView(AboutView.class, AboutView.VIEW_NAME, new IronIcon("icons",
    // "info"));

    // Add menu to the layout
    add(menu);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    attachEvent.getUI().addShortcutListener(SecurityUtil::signOut, Key.KEY_L, KeyModifier.CONTROL);
  }

  /**
   * Build and add secured menus
   *
   * @param securedClass View to secure
   * @param viewName     Name of the view
   * @param icon         Icon to apply to the menu
   */
  private void addSecuredMenu(
      Class<? extends Component> securedClass, String viewName, IronIcon icon) {
    if (SecurityUtil.isAccessGranted(securedClass)) {
      menu.addView(securedClass, viewName, icon);
    }
  }
}
