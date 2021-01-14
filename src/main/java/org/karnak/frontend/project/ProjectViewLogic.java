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
