package org.karnak.ui.data;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.karnak.data.NodeEvent;
import org.karnak.data.NodeEventType;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.Project;
import org.karnak.data.gateway.ProjectPersistence;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class ProjectDataProvider extends ListDataProvider<Project> {
    private ProjectPersistence projectPersistence;
    {
        projectPersistence = GatewayConfiguration.getInstance().getProjectPersistence();
    }

    private ApplicationEventPublisher applicationEventPublisher;

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
            refreshItem(project);
            projectPersistence.saveAndFlush(project);
            updateDestinations(project);
        }
    }

    private void updateDestinations(Project project) {
        for (Destination destination : project.getDestinations()) {
            applicationEventPublisher.publishEvent(new NodeEvent(destination, NodeEventType.UPDATE));
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

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }
}
