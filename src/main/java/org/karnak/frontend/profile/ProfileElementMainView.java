package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Comparator;
import java.util.List;
import org.karnak.backend.data.entity.ProfileElement;

public class ProfileElementMainView extends VerticalLayout {
    private List<ProfileElement> profilesOrder;

    ProfileElementMainView() {
    }

    private void profilesView() {
        removeAll();
        add(new HorizontalLayout(new H2("Profile element(s)")));  //new horizontalelayout because fix padding
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
