package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.api.ForwardNodeAPI;
import org.karnak.ui.gateway.ForwardNodeDataProvider;
import org.karnak.ui.util.UIS;

public class LayoutEditForwardNode extends VerticalLayout {
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeAPI forwardNodeAPI;
    private ForwardNode currentForwardNode;

    private EditAETitleDescription editAETitleDescription;
    private TabSourcesDestination tabSourcesDestination;
    private VerticalLayout layoutDestinationsSources;
    private GridFilterDestinations gridFilterDestinations;
    private ButtonSaveDeleteCancel buttonSaveDeleteCancel;

    public LayoutEditForwardNode(ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPI forwardNodeAPI) {
        this.forwardNodeViewLogic = forwardNodeViewLogic;
        this.forwardNodeAPI = forwardNodeAPI;
        this.currentForwardNode = null;

        setSizeFull();
        editAETitleDescription = new EditAETitleDescription();
        tabSourcesDestination = new TabSourcesDestination();
        layoutDestinationsSources = new VerticalLayout();
        layoutDestinationsSources.setSizeFull();
        gridFilterDestinations = new GridFilterDestinations(forwardNodeAPI.getDataProvider().getDataService());
        buttonSaveDeleteCancel = new ButtonSaveDeleteCancel();

        add(UIS.setWidthFull(editAETitleDescription),
                UIS.setWidthFull(tabSourcesDestination),
                UIS.setWidthFull(layoutDestinationsSources),
                UIS.setWidthFull(buttonSaveDeleteCancel));

        setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
        setEventChangeTabValue();
        setEventCancelButton();
        setEventDeleteButton();
    }

    public void load(ForwardNode forwardNode) {
        // TODO: Disable all component if forwardNode is null
        currentForwardNode = forwardNode;
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

    private void setEventCancelButton() {
        buttonSaveDeleteCancel.getCancel().addClickListener(event -> {
            forwardNodeViewLogic.cancelForwardNode();
        });
    }

    private void setEventDeleteButton() {
        buttonSaveDeleteCancel.getDelete().addClickListener(event -> {
            forwardNodeAPI.deleteForwardNode(currentForwardNode);
            forwardNodeViewLogic.cancelForwardNode();
        });
    }
}
