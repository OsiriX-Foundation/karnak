package org.karnak.backend.service.profilepipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.Profile;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.frontend.profile.ProfileError;

public abstract class ProfilePipeService implements Serializable {

    public abstract List<Profile> getAllProfiles();

    public abstract ArrayList<ProfileError> validateProfile(ProfilePipeBody profilePipeYml);

    public abstract Profile saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault);

    public abstract Profile updateProfile(Profile profile);

    public abstract void deleteProfile(Profile profile);
}
