package org.karnak.ui.about;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("KARNAK - About")
@Secured({"ROLE_ADMIN"})
@SuppressWarnings("serial")
public class AboutView extends VerticalLayout {
    public static final String VIEW_NAME = "About";

    public AboutView() {
        add(new H2("About KARNAK"));

        setSizeFull();
    }
}
