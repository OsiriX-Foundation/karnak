package org.karnak.backend.service;

import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.data.repo.ProjectRepo;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.springframework.context.ApplicationEventPublisher;

public class ProjectDataProvider extends ListDataProvider<ProjectEntity> {

    private final ProjectRepo projectRepo;

    {
        projectRepo = GatewayConfig.getInstance().getProjectPersistence();
    }

    private ApplicationEventPublisher applicationEventPublisher;

    public ProjectDataProvider() {
        this(new ArrayList<>());
    }

    public ProjectDataProvider(List<ProjectEntity> items) {
        super(items);
        getItems().addAll(getAllProjects());
    }

    public void save(ProjectEntity projectEntity) {
        boolean isNewProject = projectEntity.isNewData();
        if (isNewProject) {
            getItems().add(projectEntity);
        } else {
            refreshItem(projectEntity);
        }
        projectRepo.saveAndFlush(projectEntity);
        refreshAll();
    }

    public void update(ProjectEntity projectEntity) {
        if (!projectEntity.isNewData()) {
            projectRepo.saveAndFlush(projectEntity);
            updateDestinations(projectEntity);
            refreshAll();
        }
    }

    private void updateDestinations(ProjectEntity projectEntity) {
        for (DestinationEntity destinationEntity : projectEntity.getDestinationEntities()) {
            applicationEventPublisher
                .publishEvent(new NodeEvent(destinationEntity, NodeEventType.UPDATE));
        }
    }

    public void remove(ProjectEntity projectEntity) {
        projectRepo.deleteById(projectEntity.getId());
        projectRepo.flush();
        refreshAll();
    }

    public ProjectEntity getProjectById(Long projectID) {
        refreshAll();
        return getItems().stream()
            .filter(project -> project.getId().equals(projectID))
            .findAny().orElse(null);
    }

    public List<ProjectEntity> getAllProjects() {
        return projectRepo.findAll();
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
