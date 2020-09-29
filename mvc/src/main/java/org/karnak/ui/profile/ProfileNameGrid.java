package org.karnak.ui.profile;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.profile.Profile;

import java.util.List;

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
