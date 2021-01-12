package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.backend.service.SourceNodeDataProvider;
import org.karnak.frontend.component.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewUpdateSourceNode extends VerticalLayout {

    private final Binder<DicomSourceNodeEntity> binderFormSourceNode;
    private final SourceNodeDataProvider dataProvider;
    private ViewLogic viewLogic;
    private final FormSourceNode formSourceNode;
    private DicomSourceNodeEntity currentSourceNode;
    private final ButtonSaveDeleteCancel buttonSaveDeleteCancel;

    @Autowired
    public NewUpdateSourceNode(SourceNodeDataProvider sourceNodeDataProvider) {
        currentSourceNode = null;
        dataProvider = sourceNodeDataProvider;
        binderFormSourceNode = new BeanValidationBinder<>(DicomSourceNodeEntity.class);
        buttonSaveDeleteCancel = new ButtonSaveDeleteCancel();
        formSourceNode = new FormSourceNode(binderFormSourceNode, buttonSaveDeleteCancel);
    }

    @PostConstruct
    public void init() {
        setButtonSaveEvent();
        setButtonDeleteEvent();
    }

    public void setView() {
        removeAll();
        binderFormSourceNode.readBean(currentSourceNode);
        add(formSourceNode);
    }

    public void load(DicomSourceNodeEntity sourceNode) {
        if (sourceNode != null) {
            currentSourceNode = sourceNode;
            buttonSaveDeleteCancel.getDelete().setEnabled(true);
        } else {
            currentSourceNode = DicomSourceNodeEntity.ofEmpty();
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

    public ViewLogic getViewLogic() {
        return viewLogic;
    }

    public void setViewLogic(ViewLogic viewLogic) {
        this.viewLogic = viewLogic;
    }
}
