/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;

public class ProfileDropDown extends ComboBox<ProfileEntity> {

  private final ProfilePipeService profilePipeService;

  public ProfileDropDown() {
    profilePipeService = new ProfilePipeServiceImpl();
    updateList();
  }

  public void updateList() {
    setItems(profilePipeService.getAllProfiles());
    setItemLabelGenerator(ProfileEntity::getName);
  }
}
