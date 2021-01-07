package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import org.karnak.backend.data.entity.Argument;
import org.karnak.backend.data.entity.ProfileElement;
import org.karnak.backend.data.entity.Tag;

public class ProfileElementView extends Div {
    ProfileElement profileElement;
    public ProfileElementView(ProfileElement profileElement) {
        this.profileElement = profileElement;
        getStyle().set("margin-top", "0px");
        setView();
    }

    public void setView() {
        removeAll();
        if (profileElement.getCodename() != null) {
            add(setProfileValue("Codename : " + profileElement.getCodename()));
        }
        if (profileElement.getAction() != null) {
            add(setProfileValue("Action : " + profileElement.getAction()));
        }
        if (profileElement.getOption() != null){
            add(setProfileValue("Option : " + profileElement.getOption()));
        }
        if (profileElement.getArguments() != null && profileElement.getArguments().size() > 0){
            add(setProfileValue("Arguments"));
            add(setProfileArguments(profileElement.getArguments()));
        }
        if (profileElement.getCondition() != null) {
            add(setProfileValue("Condition : " + profileElement.getCondition()));
        }
        if (profileElement.getIncludedtag().size() > 0) {
            add(setProfileValue("Tags"));
            add(setProfileTags(profileElement.getIncludedtag()));
        }
        if (profileElement.getExceptedtags().size() > 0) {
            add(setProfileValue("Excluded tags"));
            add(setProfileTags(profileElement.getExceptedtags()));
        }
    }

    private Div setProfileValue(String value) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(value));
        profileNameDiv.getStyle().set("color", "grey").set("padding-left", "10px").set("margin-top", "5px");
        return profileNameDiv;
    }

    private VerticalLayout setProfileTags(List<? extends Tag> tags) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("margin-top", "0px");
        for (Tag tag : tags) {
            Div tagDiv = new Div();
            tagDiv.add(new Text(tag.getTagValue()));
            tagDiv.getStyle().set("color", "grey").set("padding-left", "15px").set("margin-top", "2px");
            verticalLayout.add(tagDiv);
        }
        return verticalLayout;
    }

    private VerticalLayout setProfileArguments(List<Argument> arguments) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("margin-top", "0px");
        for (Argument argument : arguments) {
            Div tagDiv = new Div();
            tagDiv.add(new Text(argument.getKey() + " : "+ argument.getValue()));
            tagDiv.getStyle().set("color", "grey").set("padding-left", "15px").set("margin-top", "2px");
            verticalLayout.add(tagDiv);
        }
        return verticalLayout;
    }
}
