package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class NewForwardNode extends HorizontalLayout {
    private Button newForwardNode;
    private TextField newAETitleForwardNode;
    private Button addNewForwardNode;
    private Button cancelNewForwardNode;

    public NewForwardNode() {
        newAETitleForwardNode = new TextField();
        addNewForwardNode = new Button("Add");
        cancelNewForwardNode= new Button("Cancel");
        newForwardNode = new Button("New forward node");
        initView();
    }

    private void initView() {
        setNewAETitleForwardNode();
        setAddNewForwardNode();
        setCancelNewForwardNode();
        setNewForwardNode();
        add(newForwardNode);
    }

    private void setNewAETitleForwardNode() {
        newAETitleForwardNode.setPlaceholder("Forward AETitle");
    }

    private void setAddNewForwardNode() {
        addNewForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        addNewForwardNode.addClickListener(click -> {
            removeAll();
            add(newForwardNode);
        });
    }

    private void setCancelNewForwardNode() {
        cancelNewForwardNode.addClickListener(click -> {
            removeAll();
            add(newForwardNode);
        });
    }

    private void setNewForwardNode() {
        newForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newForwardNode.addClickListener(click -> {
            removeAll();
            newAETitleForwardNode.setValue("");
            add(newAETitleForwardNode, addNewForwardNode, cancelNewForwardNode);
        });
        // CTRL+N will create a new window which is unavoidable
        newForwardNode.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
    }

    public Button getNewForwardNode() {
        return newForwardNode;
    }

    public TextField getNewAETitleForwardNode() {
        return newAETitleForwardNode;
    }

    public Button getAddNewForwardNode() {
        return addNewForwardNode;
    }

    public Button getCancelNewForwardNode() {
        return cancelNewForwardNode;
    }
}
