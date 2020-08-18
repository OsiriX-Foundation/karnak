package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.Argument;
import org.karnak.data.profile.ProfileElement;
import org.karnak.data.profile.Tag;

import java.util.Comparator;
import java.util.List;

public class ProfilesMetadata extends VerticalLayout {
    private List<ProfileElement> profilesOrder;

    ProfilesMetadata() {
        getStyle().set("overflow-y", "auto");
    }

    private void profilesView() {
        removeAll();
        add(new H2("Profile element(s)"));
        for (ProfileElement profileElement : profilesOrder) {
            add(setProfileName((profileElement.getPosition()+1) + ". " + profileElement.getName()));
            if (profileElement.getCodename() != null) {
                add(setProfileValue("Codename : " + profileElement.getCodename()));
            }
            if (profileElement.getArguments() != null && profileElement.getArguments().size() > 0){
                add(setProfileValue("Arguments"));
                add(setProfileArguments(profileElement.getArguments()));
            }
            if (profileElement.getCondition() != null) {
                add(setProfileValue("Condition : " + profileElement.getCondition()));
            }
            if (profileElement.getAction() != null) {
                add(setProfileValue("Action : " + profileElement.getAction()));
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
    }

    private Div setProfileName(String name) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(name));
        profileNameDiv.getStyle().set("font-weight", "bold").set("padding-left", "5px");
        return profileNameDiv;
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

    public void setProfiles(List<ProfileElement> profileElements) {
        if (profileElements != null) {
            profileElements.sort(Comparator.comparingInt(ProfileElement::getPosition));
            profilesOrder = profileElements;
            profilesView();
        }
    }
}
