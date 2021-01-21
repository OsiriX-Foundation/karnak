/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.karnak.backend.util.SecurityUtil;
import org.karnak.frontend.dicom.DicomMainView;
import org.karnak.frontend.extid.ExternalIDView;
import org.karnak.frontend.forwardnode.ForwardNodeView;
import org.karnak.frontend.help.HelpView;
import org.karnak.frontend.mainzelliste.MainzellisteView;
import org.karnak.frontend.profile.ProfileView;
import org.karnak.frontend.project.MainViewProjects;
import org.springframework.security.access.annotation.Secured;

/** The main layout. Contains the navigation menu. */
@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@JsModule("@polymer/iron-icons/iron-icons.js")
@CssImport(value = "./styles/shared-styles.css")
@Theme(value = Lumo.class)
@Route(value = "mainLayout")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class MainLayout extends FlexLayout implements RouterLayout {

  private final Menu menu;

  public MainLayout() {
    setSizeFull();
    setClassName("main-layout");

    menu = new Menu();
    menu.addView(
        ForwardNodeView.class, ForwardNodeView.VIEW_NAME, new IronIcon("icons", "settings"));
    menu.addView(ProfileView.class, ProfileView.VIEW_NAME, new IronIcon("icons", "assignment"));
    menu.addView(
        MainViewProjects.class, MainViewProjects.VIEW_NAME, new IronIcon("icons", "class"));
    menu.addView(
        ExternalIDView.class, ExternalIDView.VIEW_NAME, new IronIcon("icons", "perm-identity"));
    menu.addView(
        MainzellisteView.class, MainzellisteView.VIEW_NAME, new IronIcon("icons", "perm-identity"));
    menu.addView(DicomMainView.class, DicomMainView.VIEW_NAME, new IronIcon("icons", "build"));
    menu.addView(HelpView.class, HelpView.VIEW_NAME, new IronIcon("icons", "help"));
    // menu.addView(AboutView.class, AboutView.VIEW_NAME, new IronIcon("icons", "info"));
    add(menu);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    attachEvent.getUI().addShortcutListener(SecurityUtil::signOut, Key.KEY_L, KeyModifier.CONTROL);

    // add the admin view menu item if/when it is registered dynamically
    /*
    Command addAdminMenuItemCommand = () -> menu.addView(AdminView.class, AdminView.VIEW_NAME,
            new IronIcon("icons", "perm-identity"));
    RouteConfiguration sessionScopedConfiguration = RouteConfiguration.forSessionScope();
    if (sessionScopedConfiguration.isRouteRegistered(AdminView.class)) {
        addAdminMenuItemCommand.execute();
    } else {
        sessionScopedConfiguration.addRoutesChangeListener(event -> {
            for (RouteBaseData data : event.getAddedRoutes()) {
                if (data.getNavigationTarget().equals(AdminView.class)) {
                    addAdminMenuItemCommand.execute();
                }
            }
        });
    }
     */
  }
}
