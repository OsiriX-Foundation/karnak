package org.karnak.backend.service.profilepipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.karnak.backend.configuration.AppConfig;
import org.karnak.backend.data.entity.Argument;
import org.karnak.backend.data.entity.ExcludedTag;
import org.karnak.backend.data.entity.IncludedTag;
import org.karnak.backend.data.entity.Mask;
import org.karnak.backend.data.entity.Profile;
import org.karnak.backend.data.entity.ProfileElement;
import org.karnak.backend.data.repository.ProfilePersistence;
import org.karnak.backend.enums.ProfileItemType;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.frontend.profile.ProfileError;

public class ProfilePipeServiceImpl extends ProfilePipeService {

    private final ProfilePersistence profilePersistence;
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
    public ArrayList<ProfileError> validateProfile(ProfilePipeBody profilePipeYml) {
        Profile newProfile = createNewProfile(profilePipeYml, false);
        ArrayList<ProfileError> profileErrors = new ArrayList<>();
        for (ProfileElement profileElement : newProfile.getProfileElements()) {
            ProfileError profileError = new ProfileError(profileElement);
            profileErrors.add(profileError);
            ProfileItemType t = ProfileItemType.getType(profileElement.getCodename());
            if (t == null) {
                profileError.setError("Cannot find the profile codename: " + profileElement.getCodename());
            } else {
                try {
                    t.getProfileClass().getConstructor(ProfileElement.class).newInstance(profileElement);
                } catch (Exception e) {
                    profileError.setError(e.getCause().getMessage());
                    continue;
                }
            }
        }
        return profileErrors;
    }

    @Override
    public Profile saveProfilePipe(ProfilePipeBody profilePipeYml, Boolean byDefault) {
        Profile newProfile = createNewProfile(profilePipeYml, byDefault);
        return profilePersistence.saveAndFlush(newProfile);
    }

    private Profile createNewProfile(ProfilePipeBody profilePipeYml, Boolean byDefault) {
        final Profile newProfile = new Profile(profilePipeYml.getName(), profilePipeYml.getVersion(), profilePipeYml.getMinimumKarnakVersion(), profilePipeYml.getDefaultIssuerOfPatientID(), byDefault);
        if(profilePipeYml.getMasks() != null){
            profilePipeYml.getMasks().forEach(m -> {
                Mask mask = new Mask(m.getStationName(), m.getColor(), newProfile );
                m.getRectangles().forEach(mask::addRectangle);
                newProfile.addMask(mask);
            });
        }

        AtomicInteger profilePosition = new AtomicInteger(0);
        profilePipeYml.getProfileElements().forEach(profileBody -> {
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
                profileBody.getExcludedTags().forEach(excludedTag -> {
                    final ExcludedTag excludedTagValue = new ExcludedTag(excludedTag, profileElement);
                    profileElement.addExceptedtags(excludedTagValue);
                });
            }

            newProfile.addProfilePipe(profileElement);
            profilePosition.getAndIncrement();
        });
        return newProfile;
    }

    @Override
    public Profile updateProfile(Profile profile) {
        return profilePersistence.saveAndFlush(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        profilePersistence.deleteById(profile.getId());
        profilePersistence.flush();
    }
}
