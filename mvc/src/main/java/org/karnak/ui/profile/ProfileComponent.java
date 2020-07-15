package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.ProfilePipe;

public class ProfileComponent extends VerticalLayout {
    private ProfilePipe profilePipe;
    private ProfilePipeService profilePipeService;

    ProfileComponent(ProfilePipeService profilePipeService) {
        setSizeFull();
        this.profilePipeService = profilePipeService;
    }

    public void setProfile() {
        removeAll();
        ProfileMetadata name = new ProfileMetadata("Name", profilePipe.getName());
        name.getValidateEditButton().addClickListener(event -> {
            profilePipe.setName(name.getValue());
            profilePipeService.updateProfile(profilePipe);
        });

        ProfileMetadata version = new ProfileMetadata("Profile version", profilePipe.getVersion());
        version.getValidateEditButton().addClickListener(event -> {
            profilePipe.setVersion(version.getValue());
            profilePipeService.updateProfile(profilePipe);
        });

        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        minVersion.getValidateEditButton().addClickListener(event -> {
            profilePipe.setMinimumkarnakversion(minVersion.getValue());
            profilePipeService.updateProfile(profilePipe);
        });

        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profilePipe.getDefaultissueropatientid());
        defaultIssuerOfPatientID.getValidateEditButton().addClickListener(event -> {
            profilePipe.setDefaultissueropatientid(defaultIssuerOfPatientID.getValue());
            profilePipeService.updateProfile(profilePipe);
        });

        add(name, version, minVersion, defaultIssuerOfPatientID);
    }

    public void setEventValidate(ProfileMetadata metadata) {
        metadata.getValidateEditButton().addClickListener(event -> {
            profilePipe.setName(metadata.getValue());
        });
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
