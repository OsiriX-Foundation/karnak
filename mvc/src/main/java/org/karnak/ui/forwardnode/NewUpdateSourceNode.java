package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.gateway.DicomSourceNode;
import org.karnak.ui.component.ConfirmDialog;
import org.karnak.ui.gateway.SourceNodeDataProvider;
import org.karnak.ui.util.UIS;

public class NewUpdateSourceNode extends VerticalLayout {
    private DicomSourceNode currentSourceNode;
    private SourceNodeDataProvider dataProvider;
    private ViewLogic viewLogic;
    private FormSourceNode formSourceNode;
    private Binder<DicomSourceNode> binderFormSourceNode;
    private ButtonSaveDeleteCancel buttonSaveDeleteCancel;

    public NewUpdateSourceNode(SourceNodeDataProvider sourceNodeDataProvider, ViewLogic viewLogic) {
        currentSourceNode = null;
        dataProvider = sourceNodeDataProvider;
        this.viewLogic = viewLogic;
        binderFormSourceNode = new BeanValidationBinder<>(DicomSourceNode.class);
        formSourceNode = new FormSourceNode(binderFormSourceNode);
        buttonSaveDeleteCancel = new ButtonSaveDeleteCancel();

        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

    public void setView() {
        removeAll();
        binderFormSourceNode.readBean(currentSourceNode);
        add(formSourceNode, UIS.setWidthFull(buttonSaveDeleteCancel));
    }

    public void load(DicomSourceNode sourceNode) {
        if (sourceNode != null) {
            currentSourceNode = sourceNode;
            buttonSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentSourceNode = DicomSourceNode.ofEmpty();
            buttonSaveDeleteCancel.getDelete().setEnabled(false);
        }
        setView();
    }


    private void setButtonSaveEvent() {
        buttonSaveDeleteCancel.getSave().addClickListener(event -> {
            NodeEventType nodeEventType = currentSourceNode.isNewData() == true ? NodeEventType.ADD : NodeEventType.UPDATE;
            if (binderFormSourceNode.writeBeanIfValid(currentSourceNode)) {
                dataProvider.save(currentSourceNode);
                viewLogic.updateForwardNodeInEditView();
                viewLogic.getApplicationEventPublisher().publishEvent(new NodeEvent(currentSourceNode, nodeEventType));
            }
        });
    }

    private void setButtonDeleteEvent() {
        buttonSaveDeleteCancel.getDelete().addClickListener(event -> {
            if (currentSourceNode != null) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Are you sure to delete the DICOM source node " + currentSourceNode.getAeTitle() + "?");
                dialog.addConfirmationListener(componentEvent -> {
                    NodeEvent nodeEvent = new NodeEvent(currentSourceNode, NodeEventType.REMOVE);
                    dataProvider.delete(currentSourceNode);
                    viewLogic.updateForwardNodeInEditView();
                    viewLogic.getApplicationEventPublisher().publishEvent(nodeEvent);
                });
                dialog.open();
            }
        });
    }

    public Button getButtonCancel() {
        return buttonSaveDeleteCancel.getCancel();
    }
}
