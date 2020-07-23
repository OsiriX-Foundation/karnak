package org.karnak.ui.gateway;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.data.profile.Profile;
import org.karnak.ui.profile.ProfilePipeService;
import org.karnak.ui.profile.ProfilePipeServiceImpl;

public class ProfileDropDown extends ComboBox<Profile> {
    private ProfilePipeService profilePipeService;

    public ProfileDropDown() {
        profilePipeService = new ProfilePipeServiceImpl();
        updateList();
    }

    public void updateList() {
        setItems(profilePipeService.getAllProfiles());
        setItemLabelGenerator(Profile::getName);
    }
}
