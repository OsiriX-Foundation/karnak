package org.karnak.frontend.profile;

import org.karnak.data.profile.ProfileElement;

public class ProfileError {
    private ProfileElement profileElement;
    private String error;

    public ProfileError(ProfileElement profileElement) {
        this.profileElement = profileElement;
        this.error = null;
    }

    public ProfileError(ProfileElement profileElement, String error) {
        this.profileElement = profileElement;
        this.error = error;
    }

    public ProfileElement getProfileElement() {
        return profileElement;
    }

    public void setProfileElement(ProfileElement profileElement) {
        this.profileElement = profileElement;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
