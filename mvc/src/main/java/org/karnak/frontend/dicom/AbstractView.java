package org.karnak.frontend.dicom;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.backend.enums.MessageType;
import org.karnak.backend.model.dicom.Message;
import org.karnak.frontend.component.MessageBox;


public abstract class AbstractView extends Div {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DURATION_MSG_INFO_SUCCESS = 5000;

    protected VerticalLayout mainLayout;
	
	
	public void displayMessage(Message message) {
        MessageBox messageBox = new MessageBox(message, MessageType.NOTIFICATION_MESSAGE);
        
        Notification notification = new Notification(messageBox);
        notification.setPosition(Position.TOP_CENTER);
        notification.setDuration(DURATION_MSG_INFO_SUCCESS);
        notification.open();
    }
	
	protected void setFormResponsive(FormLayout formLayout) {
    	formLayout.setResponsiveSteps(
       	    new ResponsiveStep("0px", 1),
    	    new ResponsiveStep("350px", 2),
    	    new ResponsiveStep("1050px", 4),
    	    new ResponsiveStep("2450px", 8)
    	);
	}

}
