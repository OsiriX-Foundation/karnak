package org.karnak.ui.authentication;

import org.karnak.ui.MainLayout;
import org.karnak.ui.admin.AdminView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.karnak.ui.image.LogoKarnak;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@PageTitle("KARNAK - Login")
@CssImport(value ="./styles/shared-styles.css")
@SuppressWarnings("serial")
public class LoginScreen extends FlexLayout {
    private AccessControl accessControl;

    public LoginScreen() {
        accessControl = AccessControlFactory.getInstance().createAccessControl();
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setClassName("login-screen");

        add(buildLoginMainComponent());

        // It's ugly but it works. @see
        // https://github.com/vaadin/vaadin-login-flow/issues/53
        UI.getCurrent().getPage().executeJavaScript("document.getElementById(\"vaadinLoginUsername\").focus();");
    }

    private Component buildLoginMainComponent() {
        // login form, centered in the available part of the screen
        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(this::login);

        // layout to center login form when there is sufficient screen space
        VerticalLayout loginInformation = new VerticalLayout();
        loginInformation.setJustifyContentMode(JustifyContentMode.CENTER);
        loginInformation.setAlignItems(Alignment.CENTER);
        LogoKarnak logoKarnak = new LogoKarnak("KARNAK", "225px");
        loginInformation.add(logoKarnak);
        loginInformation.add(new H1("KARNAK"));
        loginInformation.add(loginForm);

        return loginInformation;
    }

    private void login(LoginForm.LoginEvent event) {
        if (accessControl.signIn(event.getUsername(), event.getPassword())) {
            registerAdminViewIfApplicable();
            getUI().get().navigate("");
        } else {
            event.getSource().setError(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerAdminViewIfApplicable() {
        // register the admin view dynamically only for any admin user logged in
        if (accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            RouteConfiguration.forSessionScope().setRoute(AdminView.VIEW_NAME, AdminView.class, MainLayout.class);
            // as logout will purge the session route registry, no need to
            // unregister the view on logout
        }
    }
}
