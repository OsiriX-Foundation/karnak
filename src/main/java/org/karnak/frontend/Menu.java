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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import org.karnak.backend.util.SecurityUtil;
import org.karnak.frontend.util.ToggleButtonTheme;

@SuppressWarnings("serial")
public class Menu extends FlexLayout {

	private static final String SHOW_TABS = "show-tabs";

	private final ToggleButtonTheme toggleButtonTheme;

	private Tabs tabs;

	private RadioButtonGroup<String> radioGroup;

	public Menu() {
		setClassName("menu-bar");

		// Button for toggling the menu visibility on small screens
		final Button showMenu = new Button("Menu", event -> {
			if (tabs.getClassNames().contains(SHOW_TABS)) {
				tabs.removeClassName(SHOW_TABS);
			}
			else {
				tabs.addClassName(SHOW_TABS);
			}
		});
		showMenu.setClassName("menu-button");
		showMenu.addThemeVariants(ButtonVariant.LUMO_SMALL);
		showMenu.setIcon(new Icon(VaadinIcon.MENU));
		add(showMenu);

		// container for the navigation buttons, which are added by addView()
		tabs = new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		setFlexGrow(1, tabs);
		add(tabs);

		// theme
		toggleButtonTheme = new ToggleButtonTheme();
		VerticalLayout themeLayout = new VerticalLayout(toggleButtonTheme);
		themeLayout.getElement().getStyle().set("align-items", "center");
		add(themeLayout);

		// logout menu item
		Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
		logoutButton.addClickListener(event -> SecurityUtil.signOut());

		logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		add(logoutButton);
	}

	/**
	 * Add a view to the navigation menu
	 * @param viewClass that has a {@code Route} annotation
	 * @param caption view caption in the menu
	 * @param icon view icon in the menu
	 */
	public void addView(Class<? extends Component> viewClass, String caption, Icon icon) {
		Tab tab = new Tab();
		RouterLink routerLink = new RouterLink(viewClass);
		routerLink.setClassName("menu-link");
		routerLink.add(icon);
		routerLink.add(new Span(caption));
		tab.add(routerLink);
		tabs.add(tab);
	}

}
