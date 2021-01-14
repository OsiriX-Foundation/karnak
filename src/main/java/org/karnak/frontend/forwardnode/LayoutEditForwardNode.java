package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.service.DestinationDataProvider;
import org.karnak.backend.service.ForwardNodeAPI;
import org.karnak.backend.service.SourceNodeDataProvider;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.util.UIS;

public class LayoutEditForwardNode extends VerticalLayout {

  private final ForwardNodeViewLogic forwardNodeViewLogic;
  private final ViewLogic viewLogic;
  private final ForwardNodeAPI forwardNodeAPI;
  private final Binder<ForwardNodeEntity> binderForwardNode;
  private final EditAETitleDescription editAETitleDescription;
  private final TabSourcesDestination tabSourcesDestination;
  private final VerticalLayout layoutDestinationsSources;
  private final DestinationsView destinationsView;
  private final SourceNodesView sourceNodesView;
  private final NewUpdateDestination newUpdateDestination;
  private final ButtonSaveDeleteCancel buttonForwardNodeSaveDeleteCancel;
  private final NewUpdateSourceNode newUpdateSourceNode;
  public ForwardNodeEntity currentForwardNodeEntity;
  DestinationDataProvider destinationDataProvider;
  SourceNodeDataProvider sourceNodeDataProvider;

  public LayoutEditForwardNode(
      ForwardNodeViewLogic forwardNodeViewLogic, ForwardNodeAPI forwardNodeAPI) {
    getStyle().set("overflow-y", "auto");
    this.forwardNodeViewLogic = forwardNodeViewLogic;
    this.viewLogic = new ViewLogic(this);
    this.forwardNodeAPI = forwardNodeAPI;
    this.currentForwardNodeEntity = null;
    binderForwardNode = new BeanValidationBinder<>(ForwardNodeEntity.class);

    setSizeFull();
    editAETitleDescription = new EditAETitleDescription(binderForwardNode);
    tabSourcesDestination = new TabSourcesDestination();
    layoutDestinationsSources = new VerticalLayout();
    layoutDestinationsSources.setSizeFull();
    destinationsView = new DestinationsView(forwardNodeAPI.getDataProvider().getDataService());
    sourceNodesView = new SourceNodesView(forwardNodeAPI.getDataProvider().getDataService());
    buttonForwardNodeSaveDeleteCancel = new ButtonSaveDeleteCancel();

    destinationDataProvider =
        new DestinationDataProvider(forwardNodeAPI.getDataProvider().getDataService());
    newUpdateDestination = new NewUpdateDestination(destinationDataProvider, viewLogic);

    sourceNodeDataProvider =
        new SourceNodeDataProvider(forwardNodeAPI.getDataProvider().getDataService());
    newUpdateSourceNode = new NewUpdateSourceNode(sourceNodeDataProvider, viewLogic);
    setEditView();

    setLayoutDestinationsSources(tabSourcesDestination.getSelectedTab().getLabel());
    setEventChangeTabValue();
    setEventCancelButton();
    setEventDeleteButton();
    setEventSaveButton();
    setEventBinderForwardNode();
    setEventDestination();

    setEventDestinationsViewDICOM();
    setEventDestinationsViewSTOW();
    setEventDestinationCancelButton();

    setEventNewSourceNode();
    setEventGridSourceNode();
    setEventSourceNodeCancelButton();
  }

  public void setEditView() {
    removeAll();
    add(
        UIS.setWidthFull(editAETitleDescription),
        UIS.setWidthFull(tabSourcesDestination),
        UIS.setWidthFull(layoutDestinationsSources),
        UIS.setWidthFull(buttonForwardNodeSaveDeleteCancel));
  }

  public void load(ForwardNodeEntity forwardNodeEntity) {
    currentForwardNodeEntity = forwardNodeEntity;
    editAETitleDescription.setForwardNode(forwardNodeEntity);

    viewLogic.setApplicationEventPublisher(forwardNodeAPI.getApplicationEventPublisher());
    destinationsView.setForwardNode(forwardNodeEntity);
    destinationDataProvider.setForwardNode(forwardNodeEntity);

    sourceNodesView.setForwardNode(forwardNodeEntity);
    sourceNodeDataProvider.setForwardNode(forwardNodeEntity);

    setEditView();
    if (forwardNodeEntity == null) {
      tabSourcesDestination.setEnabled(false);
      buttonForwardNodeSaveDeleteCancel.setEnabled(false);
    } else {
      tabSourcesDestination.setEnabled(true);
      buttonForwardNodeSaveDeleteCancel.setEnabled(true);
    }
  }

  private void setEventChangeTabValue() {
    tabSourcesDestination.addSelectedChangeListener(
        event -> {
          Tab selectedTab = event.getSource().getSelectedTab();
          setLayoutDestinationsSources(selectedTab.getLabel());
        });
  }

  private void setLayoutDestinationsSources(String currentTab) {
    layoutDestinationsSources.removeAll();
    if (currentTab.equals(tabSourcesDestination.LABEL_SOURCES)) {
      layoutDestinationsSources.add(sourceNodesView);
    } else if (currentTab.equals(tabSourcesDestination.LABEL_DESTINATIONS)) {
      layoutDestinationsSources.add(destinationsView);
    }
  }

  private void setEventDestinationsViewDICOM() {
    destinationsView
        .getNewDestinationDICOM()
        .addClickListener(
            event -> {
              newUpdateDestination.load(null, DestinationType.dicom);
              addFormView(newUpdateDestination);
            });
  }

  private void setEventDestinationsViewSTOW() {
    destinationsView
        .getNewDestinationSTOW()
        .addClickListener(
            event -> {
              newUpdateDestination.load(null, DestinationType.stow);
              addFormView(newUpdateDestination);
            });
  }

  private void setEventNewSourceNode() {
    sourceNodesView
        .getNewSourceNode()
        .addClickListener(
            event -> {
              newUpdateSourceNode.load(null);
              addFormView(newUpdateSourceNode);
            });
  }

  private void addFormView(Component form) {
    removeAll();
    add(form);
  }

  private void setEventCancelButton() {
    buttonForwardNodeSaveDeleteCancel
        .getCancel()
        .addClickListener(
            event -> {
              forwardNodeViewLogic.cancelForwardNode();
            });
  }

  private void setEventDeleteButton() {
    buttonForwardNodeSaveDeleteCancel
        .getDelete()
        .addClickListener(
            event -> {
              if (currentForwardNodeEntity != null) {
                ConfirmDialog dialog =
                    new ConfirmDialog(
                        "Are you sure to delete the forward node "
                            + currentForwardNodeEntity.getFwdAeTitle()
                            + " ?");
                dialog.addConfirmationListener(
                    componentEvent -> {
                      forwardNodeAPI.deleteForwardNode(currentForwardNodeEntity);
                      forwardNodeViewLogic.cancelForwardNode();
                    });
                dialog.open();
              }
            });
  }

  private void setEventBinderForwardNode() {
    binderForwardNode.addStatusChangeListener(
        event -> {
          boolean isValid = !event.hasValidationErrors();
          boolean hasChanges = binderForwardNode.hasChanges();
          buttonForwardNodeSaveDeleteCancel.getSave().setEnabled(hasChanges && isValid);
        });
  }

  private void setEventSaveButton() {
    buttonForwardNodeSaveDeleteCancel
        .getSave()
        .addClickListener(
            event -> {
              if (binderForwardNode.writeBeanIfValid(currentForwardNodeEntity)) {
                forwardNodeAPI.updateForwardNode(currentForwardNodeEntity);
                forwardNodeViewLogic.cancelForwardNode();
              }
            });
  }

  private void setEventDestination() {
    destinationsView
        .getGridDestination()
        .addItemClickListener(
            event -> {
              DestinationEntity destinationEntity = event.getItem();
              newUpdateDestination.load(destinationEntity, destinationEntity.getType());
              addFormView(newUpdateDestination);
            });
  }

  private void setEventDestinationCancelButton() {
    newUpdateDestination
        .getButtonDICOMCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
    newUpdateDestination
        .getButtonSTOWCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
  }

  private void setEventGridSourceNode() {
    sourceNodesView
        .getGridSourceNode()
        .addItemClickListener(
            event -> {
              DicomSourceNodeEntity dicomSourceNodeEntity = event.getItem();
              newUpdateSourceNode.load(dicomSourceNodeEntity);
              addFormView(newUpdateSourceNode);
            });
  }

  private void setEventSourceNodeCancelButton() {
    newUpdateSourceNode
        .getButtonCancel()
        .addClickListener(
            event -> {
              setEditView();
            });
  }
}
