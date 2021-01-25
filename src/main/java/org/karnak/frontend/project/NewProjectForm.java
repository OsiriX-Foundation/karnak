/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.frontend.forwardnode.ProfileDropDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewProjectForm extends HorizontalLayout {

  private final Binder<ProjectEntity> binder;
  private final Button buttonAdd;
  private final TextField textResearchName;
  private final ProfileDropDown profileDropDown;

  // Services
  private final ProfilePipeService profilePipeService;

  @Autowired
  public NewProjectForm(final ProfilePipeService profilePipeService) {
    this.profilePipeService = profilePipeService;

    TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
    setWidthFull();
    this.binder = textFieldsBindProject.getBinder();
    this.buttonAdd = new Button("Add");
    this.textResearchName = textFieldsBindProject.getTextResearchName();
    this.profileDropDown = textFieldsBindProject.getProfileDropDown();
    setElements();
    add(this.textResearchName, this.profileDropDown, this.buttonAdd);
    this.binder.removeBinding(textFieldsBindProject.getTextSecret());
    this.binder.bindInstanceFields(this);
  }

  private void setElements() {
    profileDropDown.setItems(profilePipeService.getAllProfiles());
    profileDropDown.setItemLabelGenerator(ProfileEntity::getName);
    textResearchName.setWidth("20%");
    textResearchName.getStyle().set("padding-right", "10px");
    textResearchName.setPlaceholder("Research Name");
  }

  public Button getButtonAdd() {
    return buttonAdd;
  }

  public void clear() {
    binder.readBean(new ProjectEntity());
  }

  public Binder<ProjectEntity> getBinder() {
    return binder;
  }
}
