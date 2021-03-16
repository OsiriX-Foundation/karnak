/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import org.karnak.backend.data.entity.ProfileElementEntity;

public class ProfileElementMainView extends VerticalLayout {

  private Set<ProfileElementEntity> profilesOrder;

  ProfileElementMainView() {}

  private void profilesView() {
    removeAll();
    add(
        new HorizontalLayout(
            new H2("Profile element(s)"))); // new horizontalelayout because fix padding
    for (ProfileElementEntity profileElementEntity : profilesOrder) {
      add(
          setProfileName(
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

  public void setProfiles(Set<ProfileElementEntity> profileElementEntities) {
    if (profileElementEntities != null) {
      profileElementEntities.stream()
          .collect(Collectors.toList())
          .sort(Comparator.comparingInt(ProfileElementEntity::getPosition));
      profilesOrder = profileElementEntities;
      profilesView();
    }
  }
}
