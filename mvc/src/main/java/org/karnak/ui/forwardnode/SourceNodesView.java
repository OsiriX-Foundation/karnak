package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.data.gateway.DestinationType;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.DataService;
import org.karnak.ui.gateway.SourceNodeDataProvider;
import org.karnak.ui.util.UIS;

public class SourceNodesView extends VerticalLayout {
    private final SourceNodeDataProvider sourceNodeDataProvider;

    private HorizontalLayout layoutFilterButton;
    private TextField filter;
    private Button newSourceNode;
    private final GridSourceNode gridSourceNode;

    private String LABEL_NEW_SOURCE_NODE = "Source";
    private String PLACEHOLDER_FILTER = "Filter properties of sources";

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

    protected void setForwardNode(ForwardNode forwardNode) {
        if (forwardNode == null) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
        sourceNodeDataProvider.setForwardNode(forwardNode);
        gridSourceNode.setDataProvider(sourceNodeDataProvider);
    }

    public Button getNewSourceNode() {
        return newSourceNode;
    }

    public GridSourceNode getGridSourceNode() {
        return gridSourceNode;
    }
}
