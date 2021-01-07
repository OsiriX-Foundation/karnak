package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import org.karnak.backend.data.entity.ProfileElement;

public class ProfileErrorView extends VerticalLayout {
    ProfileErrorView() {
        getStyle().set("overflow-y", "auto");
    }

    public void setView(ArrayList<ProfileError> profileErrors) {
        removeAll();
        add(new H2("Errors occured in profile elements"));
        for (ProfileError profileError : profileErrors) {
            ProfileElement profileElement = profileError.getProfileElement();
            Div profileName = setProfileName((profileElement.getPosition()+1) + ". " + profileElement.getName());
            add(profileName);

            if (profileError.getError() != null) {
                profileName.add(setErrorIcon());
                add(setProfileError("Error : " + profileError.getError()));
            } else {
                profileName.add(setSuccessIcon());
            }
            setProfileShowHide(profileElement);
        }
    }

    public void setView(String text) {
        removeAll();
        add(new H2("Error occured"));
        add(setProfileError(text));
    }

    public void setProfileShowHide(ProfileElement profileElement) {
        ProfileShowHide profileShowHide = new ProfileShowHide(new ProfileElementView(profileElement), false);
        profileShowHide.setTextHide("Hide profile");
        profileShowHide.setTextShow("Show profile");
        add(profileShowHide);
        profileShowHide.setView();
    }

    private IronIcon setErrorIcon() {
        IronIcon errorIcon = new IronIcon("icons", "error");
        errorIcon.setColor("red");
        errorIcon.getStyle().set("padding-left", "5px");
        return  errorIcon;
    }

    private IronIcon setSuccessIcon() {
        IronIcon errorIcon = new IronIcon("icons", "check");
        errorIcon.setColor("green");
        errorIcon.getStyle().set("padding-left", "5px");
        return  errorIcon;
    }

    private Div setProfileName(String name) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(name));
        profileNameDiv.getStyle().set("font-weight", "bold");
        return profileNameDiv;
    }

    private Div setProfileError(String error) {
        Div profileErrorDiv = new Div();
        profileErrorDiv.add(new Text(error));
        profileErrorDiv.getStyle().set("color", "red").set("margin-top", "5px");
        return profileErrorDiv;
    }

}
