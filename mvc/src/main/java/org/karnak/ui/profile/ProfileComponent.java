package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.ProfilePipe;

public class ProfileComponent extends VerticalLayout {
    private ProfilePipe profilePipe;
    ProfileComponent() {
        setSizeFull();
    }

    ProfileComponent(ProfilePipe profilePipe) {
        setSizeFull();
        setProfilePipe(profilePipe);
    }

    public void setProfile() {
        removeAll();
        ProfileMetadata name = new ProfileMetadata("Name", profilePipe.getName());
        ProfileMetadata version = new ProfileMetadata("Profile version", profilePipe.getVersion());
        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profilePipe.getDefaultissueropatientid());
        add(name, version, minVersion, defaultIssuerOfPatientID);
    }

    public ProfilePipe getProfilePipe() {
        return profilePipe;
    }

    public void setProfilePipe(ProfilePipe profilePipe) {
        this.profilePipe = profilePipe;
        if (profilePipe != null) {
            setProfile();
        }
    }

    public void setError() {
        removeAll();
        Text error = new Text("An error occured");
        add(error);
    }
}
