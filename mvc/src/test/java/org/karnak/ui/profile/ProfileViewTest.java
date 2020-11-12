package org.karnak.ui.profile;

import org.junit.jupiter.api.Test;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.ProfileElement;

import java.io.IOException;


class ProfileViewTest {
    @Test
    void name() {

        final Profile profile = new Profile("YEAHH", "0.9.1", "0.9.1", "DPA");
        final ProfileElement profileElement1 = new ProfileElement("Keep tag Source Group..", "action.on.specific.tags", null, "K", null, 0, profile);
        profileElement1.addIncludedTag(new IncludedTag("(0010,0027)", profileElement1));
        final ProfileElement profileElement2 = new ProfileElement("Remove tag PatientID", "action.on.specific.tags", null, "X", null, 0, profile);
        profileElement2.addIncludedTag(new IncludedTag("(0010,0020)", profileElement2));
        profileElement2.addExceptedtags(new ExcludedTag("(0121,1254)", profileElement2));

        profile.addProfilePipe(profileElement1);
        profile.addProfilePipe(profileElement2);

        try {
            ProfileView.exportProfile(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}