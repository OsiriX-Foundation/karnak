package org.karnak.ui.profile;

import org.karnak.data.AppConfig;
import org.karnak.data.profile.*;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfilePipeServiceImpl extends ProfilePipeService {
    private ProfilePersistence profilePersistence;
    {
        profilePersistence = AppConfig.getInstance().getProfilePersistence();
    }


    @Override
    public List<Profile> getAllProfiles() {
        List<Profile> list = new ArrayList<>();
        profilePersistence.findAll() //
                .forEach(list::add);
        return list;
    }

    @Override
    public Profile saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault) {
        Profile newProfile;

        if(byDefault){
            newProfile = new Profile(profilePipeYml.getName(), profilePipeYml.getVersion(), profilePipeYml.getMinimumKarnakVersion(), profilePipeYml.getDefaultIssuerOfPatientID(), true);
        }else{
            newProfile = new Profile(profilePipeYml.getName(), profilePipeYml.getVersion(), profilePipeYml.getMinimumKarnakVersion(), profilePipeYml.getDefaultIssuerOfPatientID());
        }


        AtomicInteger profilePosition = new AtomicInteger(0);
        profilePipeYml.getProfileElements().forEach(profileBody -> {
            // TODO: add list arguments in yaml
            ProfileElement profileElement = new ProfileElement(
                    profileBody.getName(), profileBody.getCodename(), profileBody.getCondition(), profileBody.getAction(),
                    profileBody.getOption(), profilePosition.get(), newProfile
            );

            if (profileBody.getArguments() != null) {
                profileBody.getArguments().forEach((key, value) -> {
                    final Argument argument = new Argument(key, value, profileElement);
                    profileElement.addArgument(argument);
                });
            }

            if(profileBody.getTags()!=null){
                profileBody.getTags().forEach(tag->{
                    final IncludedTag includedTagValue = new IncludedTag(tag, profileElement);
                    profileElement.addIncludedTag(includedTagValue);
                });
            }

            if(profileBody.getExcludedTags()!=null) {
                profileBody.getExcludedTags().forEach(exceptedtag -> {
                    final ExcludedTag excludedTagValue = new ExcludedTag(exceptedtag, profileElement);
                    profileElement.addExceptedtags(excludedTagValue);
                });
            }

            newProfile.addProfilePipe(profileElement);
            profilePosition.getAndIncrement();
        });
        return profilePersistence.saveAndFlush(newProfile);
    }

    @Override
    public Profile updateProfile(Profile profile) {
        return profilePersistence.saveAndFlush(profile);
    }
}
