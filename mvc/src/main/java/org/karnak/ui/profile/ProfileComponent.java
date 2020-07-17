package org.karnak.ui.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.ProfilePipe;

public class ProfileComponent extends VerticalLayout {
    private ProfilePipe profilePipe;
    private ProfilePipeService profilePipeService;
    private ProfileNameGrid profileNameGrid;

    ProfileComponent(ProfilePipeService profilePipeService, ProfileNameGrid profileNameGrid) {
        setSizeFull();
        this.profilePipeService = profilePipeService;
        this.profileNameGrid = profileNameGrid;
    }

    public void setProfile() {
        removeAll();
        H2 title = new H2("Profile metadata");
        ProfileMetadata name = new ProfileMetadata("Name", profilePipe.getName());
        name.getValidateEditButton().addClickListener(event -> {
            profilePipe.setName(name.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata version = new ProfileMetadata("Profile version", profilePipe.getVersion());
        version.getValidateEditButton().addClickListener(event -> {
            profilePipe.setVersion(version.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        minVersion.getValidateEditButton().addClickListener(event -> {
            profilePipe.setMinimumkarnakversion(minVersion.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profilePipe.getDefaultissueropatientid());
        defaultIssuerOfPatientID.getValidateEditButton().addClickListener(event -> {
            profilePipe.setDefaultissueropatientid(defaultIssuerOfPatientID.getValue());
            updatedProfilePipes();
        });

        add(title, name, version, minVersion, defaultIssuerOfPatientID);
    }

    private void updatedProfilePipes() {
        profilePipeService.updateProfile(profilePipe);
        profileNameGrid.updatedProfilePipesView();
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
        if (profilePipe != null) {
            this.profilePipe = profilePipe;
            setProfile();
        }
    }

    public void setError() {
        removeAll();
        Text error = new Text("An error occured");
        add(error);
    }
}
