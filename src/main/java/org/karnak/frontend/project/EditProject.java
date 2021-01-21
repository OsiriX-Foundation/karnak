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
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectDataProvider;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.forwardnode.ProfileDropDown;

public class EditProject extends VerticalLayout {

  private final ProjectDataProvider projectDataProvider;
  private final WarningRemoveProjectUsed dialogWarning;
  private Binder<ProjectEntity> binder;
  private TextField textProjectName;
  private ProjectSecret projectSecret;
  private ProfileDropDown profileDropDown;
  private HorizontalLayout horizontalLayoutButtons;
  private Button buttonUpdate;
  private Button buttonRemove;
  private ProjectEntity projectEntity;

  public EditProject(ProjectDataProvider projectDataProvider) {
    this.projectDataProvider = projectDataProvider;
    dialogWarning = new WarningRemoveProjectUsed();

    setEnabled(false);
    setElements();
    setEventButtonAdd();
    setEventButtonRemove();
    add(textProjectName, profileDropDown, projectSecret, horizontalLayoutButtons);
  }

  public void setProject(ProjectEntity projectEntity) {
    this.projectEntity = projectEntity;
    if (projectEntity != null) {
      binder.setBean(projectEntity);
      setEnabled(true);
    } else {
      binder.removeBean();
      clear();
      setEnabled(false);
    }
  }

  private void setEventButtonAdd() {
    buttonUpdate.addClickListener(
        event -> {
          if (projectEntity != null && binder.writeBeanIfValid(projectEntity)) {
            if (projectEntity.getDestinationEntities() != null
                && projectEntity.getDestinationEntities().size() > 0) {
              ConfirmDialog dialog =
                  new ConfirmDialog(
                      String.format(
                          "The project %s is used, are you sure you want to updated ?",
                          projectEntity.getName()));
              dialog.addConfirmationListener(
                  componentEvent -> {
                    projectDataProvider.update(projectEntity);
                  });
              dialog.open();
            } else {
              projectDataProvider.update(projectEntity);
            }
          }
        });
  }

  private void setEventButtonRemove() {
    buttonRemove.addClickListener(
        e -> {
          List<DestinationEntity> destinationEntities = projectEntity.getDestinationEntities();
          if (destinationEntities != null && destinationEntities.size() > 0) {
            dialogWarning.setText(projectEntity);
            dialogWarning.open();

          } else {
            projectDataProvider.remove(projectEntity);
            clear();
            setEnabled(false);
          }
        });
  }

  private void setElements() {
    TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
    binder = textFieldsBindProject.getBinder();
    textProjectName = textFieldsBindProject.getTextResearchName();
    profileDropDown = textFieldsBindProject.getProfileDropDown();
    projectSecret = new ProjectSecret(textFieldsBindProject.getTextSecret());

    textProjectName.setLabel("Project Name");
    textProjectName.setWidthFull();

    profileDropDown.setLabel("De-identification Profile");
    profileDropDown.setWidthFull();

    buttonUpdate = new Button("Update");
    buttonRemove = new Button("Remove");
    buttonRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
    horizontalLayoutButtons = new HorizontalLayout(buttonUpdate, buttonRemove);
  }

  private void clear() {
    binder.readBean(new ProjectEntity());
  }
}
