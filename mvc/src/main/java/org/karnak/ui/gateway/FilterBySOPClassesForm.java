package org.karnak.ui.gateway;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.SOPClassUID;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class FilterBySOPClassesForm extends HorizontalLayout {

    private final MultiselectComboBox<String> sopFilter;
    private final Checkbox filterBySOPClassesCheckbox;
    private Binder<Destination> binder;
    private DataService dataService;

    public FilterBySOPClassesForm(DataService dataService, Binder<Destination> binder) {
        this.dataService = dataService;
        this.binder = binder;
        setClassName("filters-form");

        setSizeFull();

        filterBySOPClassesCheckbox = new Checkbox();
        filterBySOPClassesCheckbox.setLabel("Authorized SOPs");
        filterBySOPClassesCheckbox.setMinWidth("25%");

        sopFilter = new MultiselectComboBox();
        sopFilter.setMinWidth("70%");
        this.updatedSopFilterItems();

        filterBySOPClassesCheckbox.addValueChangeListener(checkboxBooleanComponentValueChangeEvent -> {
            if (checkboxBooleanComponentValueChangeEvent.getValue()) {
                sopFilter.onEnabledStateChanged(true);
            } else {
                sopFilter.onEnabledStateChanged(false);
            }
        });

        this.binder.forField(sopFilter)
                .withValidator(listOfSOPFilter ->
                                (!listOfSOPFilter.isEmpty()) | (listOfSOPFilter.isEmpty() && filterBySOPClassesCheckbox.getValue() == false),
                        "No filter are applied\n")
                .bind(Destination::getSOPClassUIDFiltersName, (destination, sopClassNames) -> {
                    Set<SOPClassUID> newSOPClassUIDS = new HashSet<>();
                    sopClassNames.forEach(sopClasseName -> {
                        SOPClassUID sopClassUID = dataService.getSOPClassUIDByName(sopClasseName);
                        newSOPClassUIDS.add(sopClassUID);
                    });
                    destination.setSOPClassUIDFilters(newSOPClassUIDS);
                });


        add(filterBySOPClassesCheckbox);

        add(sopFilter);

        binder.forField(filterBySOPClassesCheckbox) //
                .bind(Destination::getFilterBySOPClasses, Destination::setFilterBySOPClasses);


    }

    public void updatedSopFilterItems() {
        final List<SOPClassUID> sopClassUIDList = dataService.getAllSOPClassUIDs();
        ArrayList<String> listOfSOPClasses = new ArrayList<>();
        sopClassUIDList.forEach(sopClassUID -> listOfSOPClasses.add(sopClassUID.getName()));
        listOfSOPClasses.sort((e1, e2) -> e1.compareTo(e2));
        sopFilter.setItems(listOfSOPClasses);
    }

}
