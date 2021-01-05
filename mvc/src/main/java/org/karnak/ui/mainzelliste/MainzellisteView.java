package org.karnak.ui.mainzelliste;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "mainzelliste", layout = MainLayout.class)
@PageTitle("KARNAK - Mainzelliste")
@Tag("mainzelliste-view")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class MainzellisteView extends HorizontalLayout {
    public static final String VIEW_NAME = "Mainzelliste pseudonym";

    public MainzellisteView() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new H2("Mainzelliste Pseudonym"), new MainzellisteAddPatient());
        add(verticalLayout);
    }
}
