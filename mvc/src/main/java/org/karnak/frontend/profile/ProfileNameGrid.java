package org.karnak.frontend.profile;

import com.vaadin.flow.component.grid.Grid;
import java.util.List;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;
import org.karnak.data.profile.Profile;

public class ProfileNameGrid extends Grid<Profile> {
    List<Profile> profiles;
    ProfilePipeService profilePipeService;

    ProfileNameGrid() {
        profilePipeService = new ProfilePipeServiceImpl();
        setSelectionMode(SelectionMode.SINGLE);
        updatedProfilePipesView();
        addColumn(Profile::getName).setHeader("Name");
        addColumn(Profile::getVersion).setHeader("Version");
    }

    public void updatedProfilePipesView(){
        profiles = profilePipeService.getAllProfiles();
        setItems(profiles);
    }

    public void selectRow(Profile row) {
        getSelectionModel().select(row);
    }
}
