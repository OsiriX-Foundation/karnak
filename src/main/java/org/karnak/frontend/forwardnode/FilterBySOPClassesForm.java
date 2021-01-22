package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import java.util.HashSet;
import java.util.Set;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.service.SOPClassUIDService;
import org.vaadin.gatanaso.MultiselectComboBox;


public class FilterBySOPClassesForm extends HorizontalLayout {

    private SOPClassUIDService sopClassUIDService;
    private final MultiselectComboBox<String> sopFilter;
    private final Checkbox filterBySOPClassesCheckbox;
    private Binder<DestinationEntity> binder;

    public FilterBySOPClassesForm() {
        this.filterBySOPClassesCheckbox = new Checkbox("Authorized SOPs");
        this.sopFilter = new MultiselectComboBox<>();
    }

    public void init(Binder<DestinationEntity> binder,
        final SOPClassUIDService sopClassUIDService) {
        this.sopClassUIDService = sopClassUIDService;
        this.binder = binder;
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

        sopFilter.setItems(sopClassUIDService.getAllSOPClassUIDsName());
    }

    private void setBinder() {
        binder.forField(sopFilter)
            .withValidator(listOfSOPFilter ->
                    !listOfSOPFilter.isEmpty() || !filterBySOPClassesCheckbox.getValue(),
                "No filter are applied\n")
            .bind(DestinationEntity::retrieveSOPClassUIDFiltersName,
                (destination, sopClassNames) -> {
                    Set<SOPClassUIDEntity> newSOPClassUIDEntities = new HashSet<>();
                    sopClassNames.forEach(sopClasseName -> {
                        SOPClassUIDEntity sopClassUIDEntity = sopClassUIDService
                            .getByName(sopClasseName);
                        newSOPClassUIDEntities.add(sopClassUIDEntity);
                    });
                    destination.setSOPClassUIDEntityFilters(newSOPClassUIDEntities);
                });

        binder.forField(filterBySOPClassesCheckbox) //
            .bind(DestinationEntity::isFilterBySOPClasses,
                DestinationEntity::setFilterBySOPClasses);
    }
}
