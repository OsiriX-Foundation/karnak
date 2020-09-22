package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.DicomSourceNode;
import org.karnak.ui.util.UIS;

public class NewUpdateSourceNode extends VerticalLayout {
    private DicomSourceNode currentSourceNode;
    private FormSourceNode formSourceNode;
    private Binder<DicomSourceNode> binderFormSourceNode;
    private ButtonSaveDeleteCancel buttonSaveDeleteCancel;

    public NewUpdateSourceNode() {
        currentSourceNode = null;
        binderFormSourceNode = new BeanValidationBinder<>(DicomSourceNode.class);
        formSourceNode = new FormSourceNode(binderFormSourceNode);
        buttonSaveDeleteCancel = new ButtonSaveDeleteCancel();
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
}
