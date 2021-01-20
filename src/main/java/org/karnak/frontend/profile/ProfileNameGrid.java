package org.karnak.frontend.profile;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.List;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class ProfileNameGrid extends Grid<ProfileEntity> {

  List<ProfileEntity> profileEntities;
  private final ProfilePipeService profilePipeService;

  @Autowired
  public ProfileNameGrid(final ProfilePipeService profilePipeService) {
    this.profilePipeService = profilePipeService;
  }

  @PostConstruct
  public void init() {
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
