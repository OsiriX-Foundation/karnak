package org.karnak.ui.gateway;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.SOPClassUID;
import org.karnak.ui.util.UIS;

import java.util.ArrayList;
import java.util.List;

public class DicomFiltersForm extends HorizontalLayout {

    private final MultiSelectListBox<String> sopFilter;
    private final Label sopFilterLabel;

    public DicomFiltersForm(DataService dataService) {

        setClassName("filters-form");

        setSizeFull();


        //SOP FILTER LAYOUT
        List<SOPClassUID> sopClassUIDList = new ArrayList<>();
        sopClassUIDList = dataService.getAllSOPClassUIDs();

        sopFilter = new MultiSelectListBox<>();
        ArrayList<String> listOfCIODS = new ArrayList<>();
        sopClassUIDList.forEach(sopClassUID -> listOfCIODS.add(sopClassUID.getName()));
        sopFilter.setItems(listOfCIODS);

        VerticalLayout sopFilterPanel = new VerticalLayout();
        sopFilterPanel.getStyle().set("overflow", "auto");
        sopFilterPanel.setHeight("100px");
        add(sopFilterPanel);

        VerticalLayout sopFilterlayout = new VerticalLayout();
        sopFilterlayout.add(sopFilter);
        sopFilterlayout.getStyle().set("margin-top", "-15px");
        sopFilterPanel.add(sopFilterlayout);
        sopFilterLabel = new Label();
        sopFilterLabel.setText("Filter by SOP");

        add(UIS.setWidthFull(new VerticalLayout(sopFilterLabel, sopFilterPanel)));
    }
}
