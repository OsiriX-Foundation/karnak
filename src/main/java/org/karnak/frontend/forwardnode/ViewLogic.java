/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.forwardnode;

import org.springframework.context.ApplicationEventPublisher;

public class ViewLogic {

  LayoutEditForwardNode currentLayout;
  private ApplicationEventPublisher applicationEventPublisher;

  public ViewLogic(LayoutEditForwardNode currentLayout) {
    this.currentLayout = currentLayout;
  }

  public ApplicationEventPublisher getApplicationEventPublisher() {
    return applicationEventPublisher;
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void updateForwardNodeInEditView() {
    this.currentLayout.load(currentLayout.currentForwardNodeEntity);
  }
}
