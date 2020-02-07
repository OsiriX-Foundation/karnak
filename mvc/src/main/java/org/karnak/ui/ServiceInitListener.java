package org.karnak.ui;

import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;
import org.karnak.ui.authentication.LoginScreen;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
@SuppressWarnings("serial")
public class ServiceInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();

        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!accessControl.isUserSignedIn() && !LoginScreen.class.equals(enterEvent.getNavigationTarget()))
                    enterEvent.rerouteTo(LoginScreen.class);
            });
        });
    }
}
