/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.project;

import com.vaadin.flow.component.UI;
import org.karnak.backend.data.entity.ProjectEntity;

public class ProjectViewLogic {

  public static Long enter(String dataIdStr) {
    try {
      Long dataId = Long.valueOf(dataIdStr);
      return dataId;
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public static void navigateProject(ProjectEntity projectEntity) {
    if (projectEntity == null) {
      UI.getCurrent().navigate(MainViewProjects.class, "");
    } else {
      String projectID = String.valueOf(projectEntity.getId());
      UI.getCurrent().navigate(MainViewProjects.class, projectID);
    }
  }
}
