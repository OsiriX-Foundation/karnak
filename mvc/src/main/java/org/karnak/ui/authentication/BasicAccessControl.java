package org.karnak.ui.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
@SuppressWarnings("serial")
public class BasicAccessControl implements AccessControl {
    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty())
            return false;

        if (!username.equals(password))
            return false;

        if (!"admin".equals(username))
            return false;

        CurrentUser.set(username);
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
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
