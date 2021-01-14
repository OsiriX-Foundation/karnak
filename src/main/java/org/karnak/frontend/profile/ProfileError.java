package org.karnak.frontend.profile;

import org.karnak.backend.data.entity.ProfileElementEntity;

public class ProfileError {

  private ProfileElementEntity profileElementEntity;
  private String error;

  public ProfileError(ProfileElementEntity profileElementEntity) {
    this.profileElementEntity = profileElementEntity;
    this.error = null;
  }

  public ProfileError(ProfileElementEntity profileElementEntity, String error) {
    this.profileElementEntity = profileElementEntity;
    this.error = error;
  }

  public ProfileElementEntity getProfileElement() {
    return profileElementEntity;
  }

  public void setProfileElement(ProfileElementEntity profileElementEntity) {
    this.profileElementEntity = profileElementEntity;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
