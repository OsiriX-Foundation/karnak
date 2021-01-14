package org.karnak.frontend.profile;

import com.vaadin.flow.component.grid.Grid;
import java.util.List;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;

public class ProfileNameGrid extends Grid<ProfileEntity> {

  List<ProfileEntity> profileEntities;
  ProfilePipeService profilePipeService;

  ProfileNameGrid() {
    profilePipeService = new ProfilePipeServiceImpl();
    setSelectionMode(SelectionMode.SINGLE);
    updatedProfilePipesView();
    addColumn(ProfileEntity::getName).setHeader("Name");
    addColumn(ProfileEntity::getVersion).setHeader("Version");
  }

  public void updatedProfilePipesView() {
    profileEntities = profilePipeService.getAllProfiles();
    setItems(profileEntities);
  }

  public void selectRow(ProfileEntity row) {
    getSelectionModel().select(row);
  }
}
