package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.ForwardNodeDataProvider;
import org.karnak.ui.util.UIS;

public class LayoutEditForwardNode extends VerticalLayout {
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeDataProvider dataProvider;
    private EditAETitleDescription editAETitleDescription;
    private TabSourcesDestination tabSourcesDestination;
    private VerticalLayout layoutDestinationsSources;
    private GridFilterDestinations gridFilterDestinations;

    public LayoutEditForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeDataProvider dataProvider) {
        setSizeFull();
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        this.dataProvider = dataProvider;

        editAETitleDescription = new EditAETitleDescription();
        tabSourcesDestination = new TabSourcesDestination();
        layoutDestinationsSources = new VerticalLayout();
        layoutDestinationsSources.setSizeFull();
        gridFilterDestinations = new GridFilterDestinations(dataProvider.getDataService());

        add(UIS.setWidthFull(editAETitleDescription),
                UIS.setWidthFull(tabSourcesDestination),
                UIS.setWidthFull(layoutDestinationsSources));

        setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
        setEventChangeTabValue();
    }

    public void load(ForwardNode forwardNode) {
        // TODO: Disable all component if forwardNode is null
        editAETitleDescription.setForwardNode(forwardNode);
        gridFilterDestinations.setForwardNode(forwardNode);
    }

    private void setEventChangeTabValue() {
        tabSourcesDestination.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSource().getSelectedTab();
            setLayoutDestinationsSources(selectedTab.getLabel());
        });
    }

    private void setLayoutDestinationsSources(String currentTab) {
        layoutDestinationsSources.removeAll();
        if (currentTab.equals(tabSourcesDestination.LABEL_SOURCES)) {
            System.out.println("SOURCE");
        } else if (currentTab.equals(tabSourcesDestination.LABEL_DESTINATIONS)) {
            layoutDestinationsSources.add(gridFilterDestinations);
        }
    }
}
