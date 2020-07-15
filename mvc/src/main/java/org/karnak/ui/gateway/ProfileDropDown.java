package org.karnak.ui.gateway;

import com.vaadin.flow.component.select.Select;
import org.karnak.data.profile.ProfilePipe;
import org.karnak.ui.profile.ProfilePipeService;
import org.karnak.ui.profile.ProfilePipeServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDropDown extends Select<String> {
    private ProfilePipeService profilePipeService;
    private List<ProfilePipe> profilePipes;

    public ProfileDropDown() {
        profilePipeService = new ProfilePipeServiceImpl();
        updateList();
    }

    public void updateList() {
        profilePipes = profilePipeService.getAllProfiles();
        List<String> listProfilePipes = profilePipesToString(profilePipes);
        setItems(listProfilePipes);
    }

    private List<String> profilePipesToString(List<ProfilePipe> profilePipes) {
        return profilePipes.stream()
                .map(profile -> profile.getName() + " - " + profile.getVersion())
                .collect(Collectors.toList());
    }

}
