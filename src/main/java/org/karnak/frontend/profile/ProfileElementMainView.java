package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Comparator;
import java.util.List;
import org.karnak.backend.data.entity.ProfileElementEntity;

public class ProfileElementMainView extends VerticalLayout {

  private List<ProfileElementEntity> profilesOrder;

    ProfileElementMainView() {
    }

    private void profilesView() {
      removeAll();
      add(new HorizontalLayout(
          new H2("Profile element(s)")));  //new horizontalelayout because fix padding
      for (ProfileElementEntity profileElementEntity : profilesOrder) {
        add(setProfileName(
            (profileElementEntity.getPosition() + 1) + ". " + profileElementEntity.getName()));
        add(new ProfileElementView(profileElementEntity));
      }
    }

  private Div setProfileName(String name) {
    Div profileNameDiv = new Div();
    profileNameDiv.add(new Text(name));
    profileNameDiv.getStyle().set("font-weight", "bold").set("padding-left", "5px");
    return profileNameDiv;
  }

  public void setProfiles(List<ProfileElementEntity> profileElementEntities) {
    if (profileElementEntities != null) {
      profileElementEntities.sort(Comparator.comparingInt(ProfileElementEntity::getPosition));
      profilesOrder = profileElementEntities;
      profilesView();
    }
  }
}
