package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.Tag;

import java.util.Comparator;
import java.util.List;

public class ProfilesMetadata extends VerticalLayout {
    private List<Profile> profilesOrder;

    ProfilesMetadata() {
        getStyle().set("overflow-y", "auto");
    }

    private void profilesView() {
        removeAll();
        add(new H2("Profile(s) pipeline used"));
        for (Profile profile : profilesOrder) {
            add(setProfileName((profile.getPosition()+1) + ". " + profile.getName()));
            if (profile.getCodename() != null) {
                add(setProfileValue("Codename : " + profile.getCodename()));
            }
            if (profile.getAction() != null) {
                add(setProfileValue("Action : " + profile.getAction()));
            }
            if (profile.getIncludedtag().size() > 0) {
                add(setProfileValue("Tags"));
                add(setProfileTags(profile.getIncludedtag()));
            }
            if (profile.getExceptedtags().size() > 0) {
                add(setProfileValue("Excluded tags"));
                add(setProfileTags(profile.getExceptedtags()));
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

    public void setProfiles(List<Profile> profiles) {
        if (profiles != null) {
            profiles.sort(Comparator.comparingInt(Profile::getPosition));
            profilesOrder = profiles;
            profilesView();
        }
    }
}
