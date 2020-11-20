package org.karnak.ui.profile;

import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ProfilePipeService implements Serializable {

    public abstract List<Profile> getAllProfiles();

    public abstract ArrayList<ProfileError> validateProfile(ProfilePipeBody profilePipeYml);

    public abstract Profile saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault);

    public abstract Profile updateProfile(Profile profile);

    public abstract void deleteProfile(Profile profile);
}
