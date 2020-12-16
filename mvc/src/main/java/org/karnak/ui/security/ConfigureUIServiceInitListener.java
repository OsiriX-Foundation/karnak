package org.karnak.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    /**
     * Listen for the initialization of the UI (the internal root component in Vaadin)
     * and then add a listener before every view transition
     *
     * @param event ServiceInitEvent
     */
    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroute all requests to the login, if the user does not have the role to see the view
     *
     * @param event BeforeEnterEvent
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
            SecurityUtils.signOut();
        }
    }

}