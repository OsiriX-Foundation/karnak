package org.karnak.frontend.component;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.backend.enums.MessageType;
import org.karnak.backend.model.dicom.Message;

public abstract class AbstractDialog extends Composite<Dialog> {

    private static final long serialVersionUID = 1L;

    protected VerticalLayout mainLayout;
    private MessageBox messageBox;
    
    
    protected abstract void createMainLayout();
    
    public void displayMessage(Message message) {
        removeMessage();
        messageBox = new MessageBox(message, MessageType.STATIC_MESSAGE);
        mainLayout.addComponentAtIndex(1, messageBox);
    }
    
    public void removeMessage() {
        if (messageBox != null) {
            mainLayout.remove(messageBox);
        }
    }
    
}
