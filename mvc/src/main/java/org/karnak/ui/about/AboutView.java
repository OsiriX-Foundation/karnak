package org.karnak.ui.about;

import org.karnak.ui.MainLayout;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
@SuppressWarnings("serial")
public class AboutView extends VerticalLayout {
    public static final String VIEW_NAME = "About";

    public AboutView() {
        add(new H2("Karnak DICOM Gateway"));

        setSizeFull();
    }
}
