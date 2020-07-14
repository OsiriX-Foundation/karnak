package org.karnak.ui.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

import java.util.Arrays;
import java.util.List;

public class ProfileNameGrid extends Grid<Profile> {
    List<Profile> profilesName;
    Grid<Profile> gridProfile;

    ProfileNameGrid() {
        profilesName = Arrays.asList(
                new Profile("Profile1"),
                new Profile("Profile2"),
                new Profile("Profile3")
        );

        setSelectionMode(SelectionMode.SINGLE);
        setItems(profilesName);
        addColumn(Profile::getName).setHeader("Name");
    }
}
