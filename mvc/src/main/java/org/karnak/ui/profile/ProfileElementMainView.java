package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.ProfileElement;

import java.util.Comparator;
import java.util.List;

public class ProfileElementMainView extends VerticalLayout {
    private List<ProfileElement> profilesOrder;

    ProfileElementMainView() {
    }

    private void profilesView() {
        removeAll();
        add(new H2("Profile element(s)"));
        for (ProfileElement profileElement : profilesOrder) {
            add(setProfileName((profileElement.getPosition()+1) + ". " + profileElement.getName()));
            add(new ProfileElementView(profileElement));
        }
    }

    private Div setProfileName(String name) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(name));
        profileNameDiv.getStyle().set("font-weight", "bold").set("padding-left", "5px");
        return profileNameDiv;
    }

    public void setProfiles(List<ProfileElement> profileElements) {
        if (profileElements != null) {
            profileElements.sort(Comparator.comparingInt(ProfileElement::getPosition));
            profilesOrder = profileElements;
            profilesView();
        }
    }
}
