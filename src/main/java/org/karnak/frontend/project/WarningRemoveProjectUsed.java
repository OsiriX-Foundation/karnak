package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;

public class WarningRemoveProjectUsed extends Dialog {

    public void setText(ProjectEntity projectEntity) {
        removeAll();
        Div divTitle = new Div();
        divTitle.setText(String.format("The project %s can't be remove", projectEntity.getName()));
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder")
            .set("padding-bottom", "10px").set("color", "red");

        Div divContent = new Div();
        Div divIntro = new Div();
        divIntro.setText("The project is used in the following destinations");
        divIntro.getStyle().set("padding-bottom", "10px");

        divContent.add(divIntro);
        if (projectEntity.getDestinationEntities() != null) {
            for (DestinationEntity destinationEntity : projectEntity.getDestinationEntities()) {
                Div divDestination = new Div();
                divDestination.setText(String.format("Type: %s, Description: %s, ForwardNode: %s",
                    destinationEntity.getType(), destinationEntity.getDescription(),
                    destinationEntity
                        .getForwardNodeEntity().getFwdAeTitle()));
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
