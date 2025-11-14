package org.karnak.frontend;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

/**
 * Application shell configuration for the Vaadin UI.
 *
 * All app-shell-level annotations such as @Push, @PWA, @Viewport, etc. must be placed on
 * a class implementing AppShellConfigurator.
 */
@Push // You can customize mode: @Push(PushMode.AUTOMATIC) or MANUAL if needed
public class AppShellConfig implements AppShellConfigurator {

	// Add other app-shell settings here later (meta tags, viewport, etc.)

}
