package org.karnak.ui.profile;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.profile.ProfilePipe;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public abstract class ProfilePipeService implements Serializable {

    public abstract List<ProfilePipe> getAllProfiles();

    public abstract ProfilePipe updateProfilePipe(ProfilePipeBody profilePipeYml);
}
