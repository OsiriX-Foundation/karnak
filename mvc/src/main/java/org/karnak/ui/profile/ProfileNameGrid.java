package org.karnak.ui.profile;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.profile.ProfilePipe;

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
    }

    public void updatedProfilePipesView(){
        profilePipes = profilePipeService.getAllProfiles();
        setItems(profilePipes);
    }
}
