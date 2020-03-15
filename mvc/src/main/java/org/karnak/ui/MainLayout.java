package org.karnak.ui;

import org.karnak.ui.about.AboutView;
import org.karnak.ui.admin.AdminView;
import org.karnak.ui.authentication.AccessControlFactory;
import org.karnak.ui.gateway.GatewayView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The main layout. Contains the navigation menu.
 */
@CssImport(value ="./styles/shared-styles.css")
@Theme(value = Lumo.class)
@PWA(name = "Karnak Gateway", shortName = "karnak")
@SuppressWarnings("serial")
public class MainLayout extends FlexLayout implements RouterLayout {
    private Menu menu;

    public MainLayout() {
        setSizeFull();
        setClassName("main-layout");

        menu = new Menu();
        menu.addView(GatewayView.class, GatewayView.VIEW_NAME, VaadinIcon.EDIT.create());
        menu.addView(AboutView.class, AboutView.VIEW_NAME, VaadinIcon.INFO_CIRCLE.create());

        add(menu);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        attachEvent.getUI().addShortcutListener(
                () -> AccessControlFactory.getInstance().createAccessControl().signOut(), Key.KEY_L,
                KeyModifier.CONTROL);

        // add the admin view menu item if/when it is registered dynamically
        Command addAdminMenuItemCommand = () -> menu.addView(AdminView.class, AdminView.VIEW_NAME,
                VaadinIcon.DOCTOR.create());
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
    }
}
