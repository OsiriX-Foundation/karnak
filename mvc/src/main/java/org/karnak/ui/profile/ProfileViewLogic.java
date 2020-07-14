package org.karnak.ui.profile;

import org.karnak.data.AppConfig;
import org.karnak.data.profile.ExceptedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.ProfilePipePersistence;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

import java.util.concurrent.atomic.AtomicInteger;

public class ProfileViewLogic {
    private final ProfilePipePersistence profilePipePersistence = AppConfig.getInstance().getProfilePipePersistence();

    public void persist(ProfilePipeBody profilePipeYml){
        org.karnak.data.profile.ProfilePipe newProfilePipe = new org.karnak.data.profile.ProfilePipe(profilePipeYml.getName(), profilePipeYml.getVersion(), profilePipeYml.getMinimumkarnakversion(), profilePipeYml.getDefaultIssuerOfPatientID());

        AtomicInteger profilePosition = new AtomicInteger(0);
        profilePipeYml.getProfiles().forEach(profileBody -> {
            org.karnak.data.profile.Profile profile = new Profile(profileBody.getName(), profileBody.getCodename(), profileBody.getAction(), profilePosition.get(), newProfilePipe);

            if (profileBody.getTags()!=null) {
                profileBody.getTags().forEach(tag->{
                    final IncludedTag includedTagValue = new IncludedTag(tag, profile);
                    //TODO normalize TAG before persist
                    profile.addIncludedTag(includedTagValue);
                });
            }

            if (profileBody.getExceptedtags()!=null) {
                profileBody.getExceptedtags().forEach(exceptedtag -> {
                    final ExceptedTag exceptedTagValue = new ExceptedTag(exceptedtag, profile);
                    //TODO normalize TAG before persist
                    profile.addExceptedtags(exceptedTagValue);
                });
            }

            newProfilePipe.addProfilePipe(profile);
            profilePosition.getAndIncrement();
        });

        profilePipePersistence.save(newProfilePipe);
    }
}
