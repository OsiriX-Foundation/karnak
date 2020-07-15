package org.karnak.ui.profile;

import org.karnak.data.AppConfig;
import org.karnak.data.profile.*;
import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfilePipeServiceImpl extends ProfilePipeService {
    private ProfilePipePersistence profilePipePersistence;
    {
        profilePipePersistence = AppConfig.getInstance().getProfilePipePersistence();
    }


    @Override
    public List<ProfilePipe> getAllProfiles() {
        List<ProfilePipe> list = new ArrayList<>();
        profilePipePersistence.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public ProfilePipe saveProfilePipe(ProfilePipeBody profilePipeYml) {
        ProfilePipe newProfilePipe = new ProfilePipe(profilePipeYml.getName(), profilePipeYml.getVersion(), profilePipeYml.getMinimumkarnakversion(), profilePipeYml.getDefaultIssuerOfPatientID());

        AtomicInteger profilePosition = new AtomicInteger(0);
        profilePipeYml.getProfiles().forEach(profileBody -> {
            Profile profile = new Profile(profileBody.getName(), profileBody.getCodename(), profileBody.getAction(), profilePosition.get(), newProfilePipe);

            if(profileBody.getTags()!=null){
                profileBody.getTags().forEach(tag->{
                    final IncludedTag includedTagValue = new IncludedTag(tag, profile);
                    //TODO normalize TAG before persist
                    profile.addIncludedTag(includedTagValue);
                });
            }

            if(profileBody.getExceptedtags()!=null) {
                profileBody.getExceptedtags().forEach(exceptedtag -> {
                    final ExceptedTag exceptedTagValue = new ExceptedTag(exceptedtag, profile);
                    //TODO normalize TAG before persist
                    profile.addExceptedtags(exceptedTagValue);
                });
            }

            newProfilePipe.addProfilePipe(profile);
            profilePosition.getAndIncrement();
        });
        return profilePipePersistence.saveAndFlush(newProfilePipe);
    }

    @Override
    public ProfilePipe updateProfile(ProfilePipe profilePipe) {
        return profilePipePersistence.saveAndFlush(profilePipe);
    }
}
