package org.karnak.ui.admin;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;

/**
 * Admin view that is registered dynamically on admin user login.
 */
@PageTitle("Admin")
@SuppressWarnings("serial")
public class AdminView extends VerticalLayout {
    public static final String VIEW_NAME = "Admin";

    public AdminView() {
        add(new H2("You are connected as an admin."));
    }
}
