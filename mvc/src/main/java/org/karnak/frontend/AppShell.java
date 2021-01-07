package org.karnak.frontend;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Karnak Gateway", shortName = "karnak", iconPath = "icons/logo.png")
public class AppShell implements AppShellConfigurator {
}