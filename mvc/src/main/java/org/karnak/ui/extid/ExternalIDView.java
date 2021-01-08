package org.karnak.ui.extid;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.cache.CachedPatient;
import org.karnak.ui.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@Secured({"ROLE_ADMIN"})
@SuppressWarnings("serial")
public class ExternalIDView extends HorizontalLayout {
    public static final String VIEW_NAME = "External pseudonym";
    private final ListDataProvider<CachedPatient> dataProvider;
    private final ExternalIDGrid externalIDGrid;
    private final Div validationStatus;
    private final ExternalIDForm externalIDForm;
    private final String LABEL_DISCLAIMER_EXTID = "WARNING: The data recorded in this grid is not guaranteed " +
            "to be stored. The data is stored for a limited period of time. " +
            "The data is deleted 7 days after addition or if the application is restarted. ";


    //https://vaadin.com/components/vaadin-grid/java-examples/assigning-data
    public ExternalIDView() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();

        Label labelDisclaimer = new Label(LABEL_DISCLAIMER_EXTID);
        labelDisclaimer.getStyle().set("color", "red");
        labelDisclaimer.setMinWidth("75%");
        labelDisclaimer.getStyle().set("right", "0px");

        externalIDGrid = new ExternalIDGrid();
        dataProvider = (ListDataProvider<CachedPatient>) externalIDGrid.getDataProvider();
        externalIDForm = new ExternalIDForm(dataProvider);
        externalIDGrid.setAddPatientButton(externalIDForm.getAddPatientButton());

        validationStatus = externalIDGrid.setBinder();

        verticalLayout.add(new H2("External Pseudonym"), labelDisclaimer, externalIDForm, validationStatus, externalIDGrid);

        add(verticalLayout);
    }

}
