package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class ProfileDropDown extends ComboBox<ProfileEntity> {

  private final ProfilePipeService profilePipeService;

  @Autowired
  public ProfileDropDown(final ProfilePipeService profilePipeService) {
    this.profilePipeService = profilePipeService;
  }

  @PostConstruct
  public void init() {
    updateList();
  }

  public void updateList() {
    setItems(profilePipeService.getAllProfiles());
    setItemLabelGenerator(ProfileEntity::getName);
  }
}
