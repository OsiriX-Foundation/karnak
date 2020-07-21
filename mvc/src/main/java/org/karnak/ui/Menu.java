package org.karnak.ui;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PreserveOnRefresh;
import org.karnak.ui.authentication.AccessControlFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import org.karnak.ui.util.ToggleButtonTheme;

@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@JsModule("@polymer/iron-icons/iron-icons.js")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class Menu extends FlexLayout {
    private static final String SHOW_TABS = "show-tabs";

    private Tabs tabs;
    private RadioButtonGroup<String> radioGroup;
    private ToggleButtonTheme toggleButtonTheme;

    public Menu() {
        setClassName("menu-bar");

        // Button for toggling the menu visibility on small screens
        final Button showMenu = new Button("Menu", event -> {
            if (tabs.getClassNames().contains(SHOW_TABS)) {
                tabs.removeClassName(SHOW_TABS);
            } else {
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

        //theme
        toggleButtonTheme = new ToggleButtonTheme();
        add(new VerticalLayout(toggleButtonTheme));

        // logout menu item
        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(event -> AccessControlFactory.getInstance().createAccessControl().signOut());

        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add(logoutButton);
    }

    /**
     * Add a view to the navigation menu
     *
     * @param viewClass that has a {@code Route} annotation
     * @param caption   view caption in the menu
     * @param icon      view icon in the menu
     */
    public void addView(Class<? extends Component> viewClass, String caption, IronIcon icon) {
        Tab tab = new Tab();
        RouterLink routerLink = new RouterLink(null, viewClass);
        routerLink.setClassName("menu-link");
        routerLink.add(icon);
        routerLink.add(new Span(caption));
        tab.add(routerLink);
        tabs.add(tab);
    }
}
