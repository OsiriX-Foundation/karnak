package org.karnak.ui.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.karnak.data.AppConfig;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
@SuppressWarnings("serial")
public class BasicAccessControl implements AccessControl {
    private final String KARNAK_ADMIN = AppConfig.getInstance().getKarnakadmin();
    private final String KARNAK_PASSWORD = AppConfig.getInstance().getKarnakpassword();

    @Override
    public boolean signIn(String username, String password) {
        CurrentUser.set(username);

        if(KARNAK_ADMIN.equals(username) && KARNAK_PASSWORD.equals(password)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals(KARNAK_ADMIN);
        }

        // All users are in all non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().reload();
    }
}
