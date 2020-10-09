package org.karnak.ui.data;

import org.karnak.data.gateway.Project;
import org.karnak.data.gateway.ProjectPersistence;

import java.util.List;

public class ProjectDataProvider {

    private ProjectPersistence projectPersistence;
    {
        projectPersistence = GatewayConfiguration.getInstance().getProjectPersistence();
    }

    public List<Project> getAllProjects() {
        return projectPersistence.findAll();
    }
}
