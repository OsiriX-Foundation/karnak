package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.profilepipe.profilebody.ProfileBody;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

public class ProfileComponent extends VerticalLayout {
    private ProfilePipeBody profilePipe;
    ProfileComponent() {
        setSizeFull();
    }

    ProfileComponent(ProfilePipeBody profilePipe) {
        setSizeFull();
        setProfilePipe(profilePipe);
    }

    public void setProfile() {
        removeAll();
        ProfileMetadata name = new ProfileMetadata("Name", profilePipe.getName());
        ProfileMetadata version = new ProfileMetadata("Profile version", profilePipe.getVersion());
        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profilePipe.getDefaultIssuerOfPatientID());
        add(name, version, minVersion, defaultIssuerOfPatientID);
    }

    public ProfilePipeBody getProfilePipe() {
        return profilePipe;
    }

    public void setProfilePipe(ProfilePipeBody profilePipe) {
        this.profilePipe = profilePipe;
        setProfile();
    }

    public void setError() {
        removeAll();
        Text error = new Text("An error occured");
        add(error);
    }
}
