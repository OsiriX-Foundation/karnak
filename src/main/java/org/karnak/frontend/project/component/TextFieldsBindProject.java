/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project.component;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.frontend.component.ProfileDropDown;

public class TextFieldsBindProject {

  private final Binder<ProjectEntity> binder;

  private final TextField textResearchName;
  private final TextField textSecret;
  private final ProfileDropDown profileDropDown;

  public TextFieldsBindProject() {
    this.textResearchName = new TextField();
    this.textSecret = new TextField();
    this.profileDropDown = new ProfileDropDown();
    this.binder = setBinder();
  }

  private Binder<ProjectEntity> setBinder() {
    Binder<ProjectEntity> binder = new BeanValidationBinder<>(ProjectEntity.class);
    binder
        .forField(textResearchName)
        .withValidator(StringUtils::isNotBlank, "Name is mandatory")
        .bind(ProjectEntity::getName, ProjectEntity::setName);
    binder
        .forField(textSecret)
        .withValidator(StringUtils::isNotBlank, "Secret is mandatory")
        .withValidator(HMAC::validateKey, "Secret is not valid")
        .bind(
            project -> {
              if (project.getSecret() != null) {
                String hexKey = HMAC.byteToHex(project.getSecret());
                return HMAC.showHexKey(hexKey);
              }
              return null;
            },
            (project, s) -> {
              project.setSecret(HMAC.hexToByte(s.replaceAll("-", "")));
            });
    binder
        .forField(profileDropDown)
        .withValidator(profilePipe -> profilePipe != null, "Choose the de-identification profile\n")
        .bind(ProjectEntity::getProfileEntity, ProjectEntity::setProfileEntity);
    return binder;
  }

  public Binder<ProjectEntity> getBinder() {
    return binder;
  }

  public TextField getTextResearchName() {
    return textResearchName;
  }

  public TextField getTextSecret() {
    return textSecret;
  }

  public ProfileDropDown getProfileDropDown() {
    return profileDropDown;
  }
}
