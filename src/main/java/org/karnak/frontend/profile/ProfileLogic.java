package org.karnak.frontend.profile;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileLogic extends ListDataProvider<ProfileEntity> {

  //view
  private ProfileView profileView;

  //services
  private final ProfilePipeService profilePipeService;

  /**
   * Autowired constructor
   *
   * @param profilePipeService Profile Pipe Service
   */
  @Autowired
  public ProfileLogic(final ProfilePipeService profilePipeService) {
    super(new ArrayList<>());
    this.profilePipeService = profilePipeService;
    initDataProvider();
  }

  @Override
  public void refreshAll() {
    getItems().clear();
    getItems().addAll(profilePipeService.getAllProfiles());
    super.refreshAll();
  }

  /** Initialize the data provider */
  private void initDataProvider() {
    getItems().addAll(profilePipeService.getAllProfiles());
  }

  public Long enter(String dataIdStr) {
    try {
      Long dataId = Long.valueOf(dataIdStr);
      return dataId;
    } catch (NumberFormatException e) {
    }
    return null;
  }



  /**
   * Retrieve a profile depending of its id
   *
   * @param profileID Id of the profile to retrieve
   * @return Project found
   */
  public ProfileEntity retrieveProfile(Long profileID) {
    refreshAll();
    return getItems().stream()
        .filter(project -> project.getId().equals(profileID))
        .findAny()
        .orElse(null);
  }

  public ProfileView getProfileView() {
    return profileView;
  }

  public void setProfileView(ProfileView profileView) {
    this.profileView = profileView;
  }
}
