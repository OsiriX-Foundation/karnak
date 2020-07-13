package org.karnak.ui.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.profilepipe.profilebody.ProfileBody;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

public class ProfileComponent extends VerticalLayout {
    ProfilePipeBody profilePipe;

    ProfileComponent(ProfilePipeBody profilePipe) {
        setSizeFull();
        this.profilePipe = profilePipe;
    }

    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        TitleValue name = new TitleValue("Name", profilePipe.getName());
        TitleValue version = new TitleValue("Profile version", profilePipe.getVersion());
        TitleValue minVersion = new TitleValue("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        TitleValue defaultIssuerOfPatientID = new TitleValue("Default issuer of PatientID", profilePipe.getDefaultIssuerOfPatientID());
        layout.add(name, version, minVersion, defaultIssuerOfPatientID);
        for (ProfileBody profile: profilePipe.getProfiles()) {
            layout.add(new TitleValue("Profile name", profile.getName()));
        }
        return layout;
    }
}
