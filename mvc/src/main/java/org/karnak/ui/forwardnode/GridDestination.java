package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import org.karnak.data.gateway.Destination;

public class GridDestination extends Grid<Destination> {
    public GridDestination() {
        setSizeFull();

        addColumn(Destination::getDescription).setHeader("Description").setFlexGrow(20).setSortable(true);

        addColumn(Destination::getType).setHeader("Type").setFlexGrow(20).setSortable(true);

        addComponentColumn(destination -> {
            Span spanDot = new Span();
            spanDot.getStyle().set("height", "25px");
            spanDot.getStyle().set("width", "25px");
            spanDot.getStyle().set("border-radius", "50%");
            spanDot.getStyle().set("display", "inline-block");
            if (destination.getState()) {
                spanDot.getStyle().set("background-color", "#5FC04C");
            } else {
                spanDot.getStyle().set("background-color", "#FC4848");         }
            return spanDot;
        }).setHeader("Enabled").setFlexGrow(20).setSortable(true);
    }

    public Destination getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Destination data) {
        getDataCommunicator().refresh(data);
    }
}
