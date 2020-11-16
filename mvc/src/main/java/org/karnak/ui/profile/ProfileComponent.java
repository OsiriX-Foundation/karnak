package org.karnak.ui.profile;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.data.profile.Profile;

public class ProfileComponent extends VerticalLayout {
    private Profile profile;
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
        ProfileMetadata name = new ProfileMetadata("Name", profile.getName(), profile.getBydefault());
        name.getValidateEditButton().addClickListener(event -> {
            profile.setName(name.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata version = new ProfileMetadata("Profile version", profile.getVersion(), profile.getBydefault());
        version.getValidateEditButton().addClickListener(event -> {
            profile.setVersion(version.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profile.getMinimumkarnakversion(), profile.getBydefault());
        minVersion.getValidateEditButton().addClickListener(event -> {
            profile.setMinimumkarnakversion(minVersion.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profile.getDefaultissueropatientid(), false);
        defaultIssuerOfPatientID.getValidateEditButton().addClickListener(event -> {
            profile.setDefaultissueropatientid(defaultIssuerOfPatientID.getValue());
            updatedProfilePipes();
        });

        ProfileMasksView profileMasksView = new ProfileMasksView(profile.getMasks());

        add(title, name, version, minVersion, defaultIssuerOfPatientID, profileMasksView);
    }

    private void updatedProfilePipes() {
        profilePipeService.updateProfile(profile);
        profileNameGrid.updatedProfilePipesView();
    }

    public void setEventValidate(ProfileMetadata metadata) {
        metadata.getValidateEditButton().addClickListener(event -> {
            profile.setName(metadata.getValue());
        });
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        if (profile != null) {
            this.profile = profile;
            setProfile();
        }
    }
}
