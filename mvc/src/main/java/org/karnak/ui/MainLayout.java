package org.karnak.ui;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.karnak.ui.authentication.AccessControlFactory;
import org.karnak.ui.dicom.DicomMainView;
import org.karnak.ui.forwardnode.ForwardNodeView;
import org.karnak.ui.extid.ExternalIDView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.karnak.ui.help.HelpView;
import org.karnak.ui.profile.ProfileView;
import org.karnak.ui.project.MainViewProjects;


/**
 * The main layout. Contains the navigation menu.
 */
@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@JsModule("@polymer/iron-icons/iron-icons.js")
@CssImport(value ="./styles/shared-styles.css")
@Theme(value = Lumo.class)
@Route(value="mainLayout")
@SuppressWarnings("serial")
public class MainLayout extends FlexLayout implements RouterLayout {
    private Menu menu;

    public MainLayout() {
        setSizeFull();
        setClassName("main-layout");

        menu = new Menu();
        menu.addView(ForwardNodeView.class, ForwardNodeView.VIEW_NAME, new IronIcon("icons", "settings"));
        menu.addView(ProfileView.class, ProfileView.VIEW_NAME, new IronIcon("icons", "assignment"));
        menu.addView(MainViewProjects.class, MainViewProjects.VIEW_NAME, new IronIcon("icons", "class"));
        menu.addView(ExternalIDView.class, ExternalIDView.VIEW_NAME, new IronIcon("icons", "social:person-add"));
        menu.addView(DicomMainView.class, DicomMainView.VIEW_NAME, new IronIcon("icons", "build"));
        menu.addView(HelpView.class, HelpView.VIEW_NAME, new IronIcon("icons", "help"));
        //menu.addView(AboutView.class, AboutView.VIEW_NAME, new IronIcon("icons", "info"));
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
