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
    private Binder<Destination> binder;
    private DataService dataService;

    public FilterBySOPClassesForm(DataService dataService, Binder<Destination> binder) {
        this.dataService = dataService;
        this.binder = binder;
        setClassName("filters-form");

        setSizeFull();



        sopFilter = new MultiselectComboBox();
        this.updatedSopFilterItems();

        this.binder.forField(sopFilter).bind(Destination::getSOPClassUIDFiltersName, (destination, sopClassNames) -> {
            ArrayList<SOPClassUID> newSOPClassUIDS = new ArrayList<>();
            sopClassNames.forEach(sopClasseName -> {
                SOPClassUID sopClassUID = dataService.getSOPClassUIDByName(sopClasseName);
                newSOPClassUIDS.add(sopClassUID);
            });
            destination.setSOPClassUIDFilters(newSOPClassUIDS);
        });

        add(sopFilter);
    }

    public void updatedSopFilterItems(){
        final List<SOPClassUID> sopClassUIDList = dataService.getAllSOPClassUIDs();
        ArrayList<String> listOfSOPClasses = new ArrayList<>();
        sopClassUIDList.forEach(sopClassUID -> listOfSOPClasses.add(sopClassUID.getName()));
        listOfSOPClasses.sort((e1, e2) -> e1.compareTo(e2));
        sopFilter.setItems(listOfSOPClasses);
    }

}
