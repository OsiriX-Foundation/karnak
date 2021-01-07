package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import java.util.HashSet;
import java.util.Set;
import org.karnak.backend.data.entity.Destination;
import org.karnak.backend.data.entity.SOPClassUID;
import org.karnak.backend.service.SOPClassUIDDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

public class FilterBySOPClassesForm extends HorizontalLayout {
    private final SOPClassUIDDataProvider sopClassUIDDataProvider;
    private final MultiselectComboBox<String> sopFilter;
    private final Checkbox filterBySOPClassesCheckbox;
    private final Binder<Destination> binder;

    public FilterBySOPClassesForm(Binder<Destination> binder) {
        this.binder = binder;
        sopClassUIDDataProvider = new SOPClassUIDDataProvider();
        filterBySOPClassesCheckbox = new Checkbox("Authorized SOPs");
        sopFilter = new MultiselectComboBox<>();
        setElements();
        setBinder();
        add(filterBySOPClassesCheckbox, sopFilter);
    }

    private void setElements() {
        filterBySOPClassesCheckbox.setMinWidth("25%");
        sopFilter.setMinWidth("70%");

        filterBySOPClassesCheckbox.setValue(false);
        sopFilter.onEnabledStateChanged(false);

        filterBySOPClassesCheckbox.addValueChangeListener(checkboxBooleanComponentValueChangeEvent ->
            sopFilter.onEnabledStateChanged(checkboxBooleanComponentValueChangeEvent.getValue())
        );

        sopFilter.setItems(sopClassUIDDataProvider.getAllSOPClassUIDsName());
    }

    private void setBinder() {
        binder.forField(sopFilter)
                .withValidator(listOfSOPFilter ->
                                !listOfSOPFilter.isEmpty() || !filterBySOPClassesCheckbox.getValue(),
                        "No filter are applied\n")
                .bind(Destination::getSOPClassUIDFiltersName, (destination, sopClassNames) -> {
                    Set<SOPClassUID> newSOPClassUIDS = new HashSet<>();
                    sopClassNames.forEach(sopClasseName -> {
                        SOPClassUID sopClassUID = sopClassUIDDataProvider.getByName(sopClasseName);
                        newSOPClassUIDS.add(sopClassUID);
                    });
                    destination.setSOPClassUIDFilters(newSOPClassUIDS);
                });

        binder.forField(filterBySOPClassesCheckbox) //
                .bind(Destination::getFilterBySOPClasses, Destination::setFilterBySOPClasses);
    }
}
