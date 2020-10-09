package org.karnak.ui.data;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.data.gateway.Project;
import org.karnak.data.gateway.ProjectPersistence;

import java.util.ArrayList;
import java.util.List;

public class ProjectDataProvider extends ListDataProvider<Project> {
    private ProjectPersistence projectPersistence;
    {
        projectPersistence = GatewayConfiguration.getInstance().getProjectPersistence();
    }

    public ProjectDataProvider() {
        this(new ArrayList<>());
    }

    public ProjectDataProvider(List<Project> items) {
        super(items);
        getItems().addAll(getAllProjects());
    }

    public void save(Project project) {
        boolean isNewProject = project.isNewData();
        if (isNewProject) {
            getItems().add(project);
            refreshAll();
        } else {
            refreshItem(project);
        }
        projectPersistence.saveAndFlush(project);
    }

    public void update(Project project) {
        if (!project.isNewData()) {
            projectPersistence.saveAndFlush(project);
        }
    }

    public void remove(Project project) {
        projectPersistence.deleteById(project.getId());
        projectPersistence.flush();
        getItems().remove(project);
        refreshAll();
    }

    public List<Project> getAllProjects() {
        return projectPersistence.findAll();
    }
}
