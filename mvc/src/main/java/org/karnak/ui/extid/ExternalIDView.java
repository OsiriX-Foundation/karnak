package org.karnak.ui.extid;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.cache.Patient;
import org.karnak.ui.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@Secured({"ROLE_ADMIN"})
@SuppressWarnings("serial")
public class ExternalIDView extends HorizontalLayout {
    public static final String VIEW_NAME = "External pseudonym";

    private final ListDataProvider<Patient> dataProvider;
    private final ExternalIDGrid grid;
    private final AddNewPatientForm addNewPatientForm;
    private final Div validationStatus;


    //https://vaadin.com/components/vaadin-grid/java-examples/assigning-data
    public ExternalIDView() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();

        grid = new ExternalIDGrid();
        dataProvider = (ListDataProvider<Patient>) grid.getDataProvider();
        addNewPatientForm = new AddNewPatientForm(dataProvider);
        grid.setAddNewPatientButton(addNewPatientForm.getAddNewPatientButton());

        validationStatus = grid.setBinder();

        verticalLayout.add(new H2("External Pseudonym"), addNewPatientForm, validationStatus, grid);

        add(verticalLayout);
    }

}
