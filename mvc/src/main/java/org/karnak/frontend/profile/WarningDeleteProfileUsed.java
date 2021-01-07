package org.karnak.frontend.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import org.karnak.data.gateway.Project;
import org.karnak.data.profile.Profile;

public class WarningDeleteProfileUsed extends Dialog {
    public void setText(Profile profile) {
        removeAll();
        Div divTitle = new Div();
        divTitle.setText(String.format("The profile %s can't be remove", profile.getName()));
        divTitle.getStyle().set("font-size", "large").set("font-weight", "bolder").set("padding-bottom", "10px").set("color", "red");

        Div divContent = new Div();
        Div divIntro = new Div();
        divIntro.setText("The profile is used in the following project(s)");
        divIntro.getStyle().set("padding-bottom", "10px");

        divContent.add(divIntro);
        if (profile.getProject() != null) {
            for (Project project : profile.getProject()) {
                Div divProject = new Div();
                divProject.setText(String.format("Project: %s", project.getName()));
                divProject.getStyle().set("padding-left", "20px").set("padding-bottom", "5px");
                divContent.add(divProject);
            }
        }

        Button cancelButton = new Button("Cancel", event -> {
            close();
        });

        cancelButton.getStyle().set("margin-left", "75%");
        add(divTitle, divContent, cancelButton);

    }
}
