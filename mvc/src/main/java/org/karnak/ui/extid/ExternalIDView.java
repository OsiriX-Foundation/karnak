package org.karnak.ui.extid;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;

@Route(value = "extid", layout = MainLayout.class)
@PageTitle("KARNAK - External ID")
@Tag("extid-view")
@SuppressWarnings("serial")
public class ExternalIDView extends HorizontalLayout {
    public static final String VIEW_NAME = "External pseudonym";

    private ListDataProvider<Patient> dataProvider;
    private ExternalIDGrid grid;
    private AddNewPatientForm addNewPatientForm;
    private Div validationStatus;


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
