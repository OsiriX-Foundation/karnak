package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.api.ForwardNodeAPI;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.util.UIS;

public class LayoutEditForwardNode extends VerticalLayout {
    private ForwardNodeViewLogic forwardNodeViewLogic;
    private ForwardNodeAPI forwardNodeAPI;
    private ForwardNode currentForwardNode;

    private EditAETitleDescription editAETitleDescription;
    private TabSourcesDestination tabSourcesDestination;
    private VerticalLayout layoutDestinationsSources;
    private DestinationsView destinationsView;
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
        destinationsView = new DestinationsView(forwardNodeAPI.getDataProvider().getDataService());
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
        currentForwardNode = forwardNode;
        editAETitleDescription.setForwardNode(forwardNode);
        destinationsView.setForwardNode(forwardNode);

        if (forwardNode == null) {
            tabSourcesDestination.setEnabled(false);
            buttonSaveDeleteCancel.setEnabled(false);
        } else {
            tabSourcesDestination.setEnabled(true);
            buttonSaveDeleteCancel.setEnabled(true);
        }
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
            layoutDestinationsSources.add(destinationsView);
        }
    }

    private void setEventCancelButton() {
        buttonSaveDeleteCancel.getCancel().addClickListener(event -> {
            forwardNodeViewLogic.cancelForwardNode();
        });
    }

    private void setEventDeleteButton() {
        buttonSaveDeleteCancel.getDelete().addClickListener(event -> {

            if (currentForwardNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the forward node " + currentForwardNode.getFwdAeTitle() + " ?");
                dialog.addConfirmationListener(componentEvent -> {
                    forwardNodeAPI.deleteForwardNode(currentForwardNode);
                    forwardNodeViewLogic.cancelForwardNode();
                });
                dialog.open();
            }
        });
    }

    private void setEventSaveButton() {
        buttonSaveDeleteCancel.getSave().addClickListener(event -> {
            // forwardNodeAPI.saveForwardNode();
        });
    }
}
