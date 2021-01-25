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
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import org.karnak.backend.data.entity.ProfileElementEntity;

public class ProfileErrorView extends VerticalLayout {

  ProfileErrorView() {
    getStyle().set("overflow-y", "auto");
  }

  public void setView(ArrayList<ProfileError> profileErrors) {
    removeAll();
    add(new H2("Errors occured in profile elements"));
    for (ProfileError profileError : profileErrors) {
      ProfileElementEntity profileElementEntity = profileError.getProfileElement();
      Div profileName =
          setProfileName(
              (profileElementEntity.getPosition() + 1) + ". " + profileElementEntity.getName());
      add(profileName);

      if (profileError.getError() != null) {
        profileName.add(setErrorIcon());
        add(setProfileError("Error : " + profileError.getError()));
      } else {
        profileName.add(setSuccessIcon());
      }
      setProfileShowHide(profileElementEntity);
    }
  }

  public void setView(String text) {
    removeAll();
    add(new H2("Error occured"));
    add(setProfileError(text));
  }

  public void setProfileShowHide(ProfileElementEntity profileElementEntity) {
    ProfileShowHide profileShowHide =
        new ProfileShowHide(new ProfileElementView(profileElementEntity), false);
    profileShowHide.setTextHide("Hide profile");
    profileShowHide.setTextShow("Show profile");
    add(profileShowHide);
    profileShowHide.setView();
  }

  private IronIcon setErrorIcon() {
    IronIcon errorIcon = new IronIcon("icons", "error");
    errorIcon.setColor("red");
    errorIcon.getStyle().set("padding-left", "5px");
    return errorIcon;
  }

  private IronIcon setSuccessIcon() {
    IronIcon errorIcon = new IronIcon("icons", "check");
    errorIcon.setColor("green");
    errorIcon.getStyle().set("padding-left", "5px");
    return errorIcon;
  }

  private Div setProfileName(String name) {
    Div profileNameDiv = new Div();
    profileNameDiv.add(new Text(name));
    profileNameDiv.getStyle().set("font-weight", "bold");
    return profileNameDiv;
  }

  private Div setProfileError(String error) {
    Div profileErrorDiv = new Div();
    profileErrorDiv.add(new Text(error));
    profileErrorDiv.getStyle().set("color", "red").set("margin-top", "5px");
    return profileErrorDiv;
  }
}
