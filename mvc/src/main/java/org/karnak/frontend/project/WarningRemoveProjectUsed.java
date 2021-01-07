package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.Project;

public class WarningRemoveProjectUsed extends Dialog {
    public void setText(Project project) {
        removeAll();
        Div divTitle = new Div();
        divTitle.setText(String.format("The project %s can't be remove", project.getName()));
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px").set("color", "red");

        Div divContent = new Div();
        Div divIntro = new Div();
        divIntro.setText("The project is used in the following destinations");
        divIntro.getStyle().set("padding-bottom", "10px");

        divContent.add(divIntro);
        if (project.getDestinations() != null) {
            for (Destination destination : project.getDestinations()) {
                Div divDestination = new Div();
                divDestination.setText(String.format("Type: %s, Description: %s, ForwardNode: %s",
                        destination.getType(), destination.getDescription(), destination.getForwardNode().getFwdAeTitle()));
                divDestination.getStyle().set("padding-left", "20px").set("padding-bottom", "5px");
                divContent.add(divDestination);
            }
        }

        Button cancelButton = new Button("Cancel", event -> {
            close();
        });

        cancelButton.getStyle().set("margin-left", "75%");
        add(divTitle, divContent, cancelButton);

    }
}
