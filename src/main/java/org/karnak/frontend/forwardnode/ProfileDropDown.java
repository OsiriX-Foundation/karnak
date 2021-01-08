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
