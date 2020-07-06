package org.karnak.ui.gateway;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.SOPClassUID;
import org.karnak.ui.util.UIS;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.List;

public class FilterBySOPClassesForm extends HorizontalLayout {

    private final MultiselectComboBox<String> sopFilter;
    private final Label sopFilterLabel;
    private Binder<Destination> binder;

    public FilterBySOPClassesForm(DataService dataService, Binder<Destination> binder) {
        this.binder = binder;
        setClassName("filters-form");

        setSizeFull();

        List<SOPClassUID> sopClassUIDList = new ArrayList<>();
        sopClassUIDList = dataService.getAllSOPClassUIDs();

        sopFilter = new MultiselectComboBox();
        ArrayList<String> listOfCIODS = new ArrayList<>();
        sopClassUIDList.forEach(sopClassUID -> listOfCIODS.add(sopClassUID.getName()));
        sopFilter.setItems(listOfCIODS);

        VerticalLayout sopFilterPanel = new VerticalLayout();
        add(sopFilterPanel);

        VerticalLayout sopFilterlayout = new VerticalLayout();
        sopFilterlayout.add(sopFilter);
        sopFilterPanel.add(sopFilterlayout);
        sopFilterLabel = new Label();
        sopFilterLabel.setText("Filter by SOP");

        this.binder.forField(sopFilter).bind(Destination::getSOPClassUIDFiltersName, (destination, sopClassNames) -> {
            ArrayList<SOPClassUID> newSOPClassUIDS= new ArrayList<>();
            sopClassNames.forEach(sopClasseName -> {
                SOPClassUID sopClassUID = dataService.getSOPClassUIDByName(sopClasseName);
                newSOPClassUIDS.add(sopClassUID);
            });
            destination.setSOPClassUIDFilters(newSOPClassUIDS);
        });

        add(UIS.setWidthFull(new VerticalLayout(sopFilterLabel, sopFilterPanel)));
    }
}
