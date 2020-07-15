package org.karnak.ui.gateway;

import com.vaadin.flow.component.select.Select;
import org.karnak.data.profile.ProfilePipe;
import org.karnak.ui.profile.ProfilePipeService;
import org.karnak.ui.profile.ProfilePipeServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDropDown extends Select<ProfilePipe> {
    private ProfilePipeService profilePipeService;
    private List<ProfilePipe> profilePipes;

    public ProfileDropDown() {
        profilePipeService = new ProfilePipeServiceImpl();
        updateList();
    }

    public void updateList() {
        profilePipes = profilePipeService.getAllProfiles();
        setItems(profilePipeService.getAllProfiles());
        setItemLabelGenerator(ProfilePipe::getName);
        setValue(getValue());
    }

}
