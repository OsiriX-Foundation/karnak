package org.karnak.ui.profile;

import com.vaadin.flow.component.grid.Grid;
import org.karnak.data.profile.ProfilePipe;

import java.util.ArrayList;
import java.util.List;

public class ProfileNameGrid extends Grid<Profile> {
    List<Profile> profilesName;
    Grid<Profile> gridProfile;

    ProfileNameGrid() {
        ProfilePipeService profilePipeService = new ProfilePipeServiceImpl();
        List<ProfilePipe> profilePipes = profilePipeService.getAllProfiles();

        profilesName = new ArrayList<>();
        profilePipes.forEach(profilePipe -> {
            profilesName.add(new Profile(profilePipe.getName()));
        });


        setSelectionMode(SelectionMode.SINGLE);
        setItems(profilesName);
        addColumn(Profile::getName).setHeader("Name");
    }
}
