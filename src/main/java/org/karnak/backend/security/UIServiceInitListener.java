/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import java.util.Objects;
import org.karnak.backend.util.SecurityUtil;
import org.springframework.stereotype.Component;

@Component
public class UIServiceInitListener implements VaadinServiceInitListener {

  /**
   * Listen for the initialization of the UI (the internal root component in Vaadin) and then add a
   * listener before every view transition
   *
   * @param event ServiceInitEvent
   */
  @Override
  public void serviceInit(ServiceInitEvent event) {
    event
        .getSource()
        .addUIInitListener(
            uiEvent -> {
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
    boolean isLoginScreen =
        Objects.equals(
            event.getNavigationTarget().getName(),
            "org.karnak.frontend.authentication.LoginScreen");

    if (!SecurityUtil.isAccessGranted(event.getNavigationTarget()) && !isLoginScreen) {
      SecurityUtil.signOut();
    }
  }
}
