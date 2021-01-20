package org.karnak.frontend.extid;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.cache.CachedPatient;
import org.karnak.frontend.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class ExternalIDView extends HorizontalLayout {
    public static final String VIEW_NAME = "External pseudonym";
    private final ListDataProvider<CachedPatient> dataProvider;
    private final ExternalIDGrid externalIDGrid;
    private final Div validationStatus;
    private final AddNewPatientForm addNewPatientForm;


    //https://vaadin.com/components/vaadin-grid/java-examples/assigning-data
    @Autowired
    public ExternalIDView(final ExternalIDGrid externalIDGrid,
        final AddNewPatientForm addNewPatientForm) {
        this.externalIDGrid = externalIDGrid;

        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();

        this.dataProvider = (ListDataProvider<CachedPatient>) this.externalIDGrid.getDataProvider();
        this.addNewPatientForm = addNewPatientForm;
        this.addNewPatientForm.init(dataProvider);

        this.externalIDGrid.setAddNewPatientButton(addNewPatientForm.getAddNewPatientButton());

        validationStatus = this.externalIDGrid.setBinder();

        verticalLayout.add(new H2("External Pseudonym"), addNewPatientForm, validationStatus,
            this.externalIDGrid);

        add(verticalLayout);
    }

}
