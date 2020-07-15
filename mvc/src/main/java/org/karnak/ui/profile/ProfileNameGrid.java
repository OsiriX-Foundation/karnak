package org.karnak.ui.profile;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.profile.ProfilePipe;

import java.util.ArrayList;
import java.util.List;

public class ProfileNameGrid extends Grid<ProfilePipe> {
    List<ProfilePipe> profilePipes;
    ProfilePipeService profilePipeService;

    ProfileNameGrid() {
        profilePipeService = new ProfilePipeServiceImpl();
        setSelectionMode(SelectionMode.SINGLE);
        updatedProfilePipesView();
        addColumn(ProfilePipe::getName).setHeader("Name");
        addColumn(ProfilePipe::getVersion).setHeader("Version");
        addColumn(ProfilePipe::getMinimumkarnakversion).setHeader("Min. Karnak Version");
        addColumn(ProfilePipe::getDefaultissueropatientid).setHeader("DeffaultIssuerOfPatientID");
    }

    public void updatedProfilePipesView(){
        profilePipes = profilePipeService.getAllProfiles();
        setItems(profilePipes);
    }
}
