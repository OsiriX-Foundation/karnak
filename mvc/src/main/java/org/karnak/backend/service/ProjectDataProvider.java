package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.configuration.GatewayConfiguration;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.Project;
import org.karnak.data.gateway.ProjectPersistence;
import org.springframework.context.ApplicationEventPublisher;

public class ProjectDataProvider extends ListDataProvider<Project> {

    private final ProjectPersistence projectPersistence;
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
        } else {
            refreshItem(project);
        }
        projectPersistence.saveAndFlush(project);
        refreshAll();
    }

    public void update(Project project) {
        if (!project.isNewData()) {
            projectPersistence.saveAndFlush(project);
            updateDestinations(project);
            refreshAll();
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
        refreshAll();
    }

    public Project getProjectById(Long projectID) {
        refreshAll();
        return getItems().stream()
                .filter(project -> project.getId().equals(projectID))
                .findAny().orElse(null);
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

    @Override
    public void refreshAll() {
        getItems().clear();
        getItems().addAll(getAllProjects());
        super.refreshAll();
    }
}
