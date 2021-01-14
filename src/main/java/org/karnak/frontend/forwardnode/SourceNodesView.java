package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.DataService;
import org.karnak.backend.service.SourceNodeDataProvider;
import org.karnak.frontend.util.UIS;

public class SourceNodesView extends VerticalLayout {

    private final SourceNodeDataProvider sourceNodeDataProvider;

    private final HorizontalLayout layoutFilterButton;
    private final TextField filter;
    private final Button newSourceNode;
    private final GridSourceNode gridSourceNode;

    private final String LABEL_NEW_SOURCE_NODE = "Source";
    private final String PLACEHOLDER_FILTER = "Filter properties of sources";

    public SourceNodesView(DataService dataService) {
        setSizeFull();
        sourceNodeDataProvider = new SourceNodeDataProvider(dataService);
        gridSourceNode = new GridSourceNode();
        filter = new TextField();
        newSourceNode = new Button(LABEL_NEW_SOURCE_NODE);
        layoutFilterButton = new HorizontalLayout(filter, newSourceNode);
        layoutFilterButton.setVerticalComponentAlignment(Alignment.START, filter);
        layoutFilterButton.expand(filter);

        setTextFieldFilter();
        setButtonNewDestinationDICOM();

        add(UIS.setWidthFull(layoutFilterButton),
                UIS.setWidthFull(gridSourceNode));

    }

    private void setTextFieldFilter() {
        filter.setPlaceholder(PLACEHOLDER_FILTER);
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> sourceNodeDataProvider.setFilter(event.getValue()));
    }

    private void setButtonNewDestinationDICOM() {
        newSourceNode.getElement().setAttribute("title", "New destination of type dicom");
        newSourceNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newSourceNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
    }

    protected void setForwardNode(ForwardNodeEntity forwardNodeEntity) {
        setEnabled(forwardNodeEntity != null);
        sourceNodeDataProvider.setForwardNode(forwardNodeEntity);
        gridSourceNode.setDataProvider(sourceNodeDataProvider);
    }

    public Button getNewSourceNode() {
        return newSourceNode;
    }

    public GridSourceNode getGridSourceNode() {
        return gridSourceNode;
    }
}
