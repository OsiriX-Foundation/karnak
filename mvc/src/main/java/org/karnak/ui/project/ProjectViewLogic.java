package org.karnak.ui.project;

import com.vaadin.flow.component.UI;
import org.karnak.data.gateway.Project;

public class ProjectViewLogic {
    public static Long enter(String dataIdStr) {
        try {
            Long dataId = Long.valueOf(dataIdStr);
            return dataId;
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public static void navigateProject(Project project) {
        if (project == null) {
            UI.getCurrent().navigate(MainViewProjects.class, "");
        } else {
            String projectID = String.valueOf(project.getId());
            UI.getCurrent().navigate(MainViewProjects.class, projectID);
        }
    }
}
