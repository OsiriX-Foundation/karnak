package org.karnak.backend.service.profilepipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.frontend.profileEntity.ProfileError;

public abstract class ProfilePipeService implements Serializable {

  public abstract List<ProfileEntity> getAllProfiles();

    public abstract ArrayList<ProfileError> validateProfile(ProfilePipeBody profilePipeYml);

  public abstract ProfileEntity saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault);

  public abstract ProfileEntity updateProfile(ProfileEntity profileEntity);

  public abstract void deleteProfile(ProfileEntity profileEntity);
}
