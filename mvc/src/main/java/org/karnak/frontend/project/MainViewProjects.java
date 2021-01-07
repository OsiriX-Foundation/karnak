package org.karnak.frontend.project;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.service.ProjectDataProvider;
import org.karnak.data.gateway.Project;
import org.karnak.frontend.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;

@Route(value = "projects", layout = MainLayout.class)
@PageTitle("KARNAK - Projects")
@Secured({"ADMIN"})
public class MainViewProjects extends HorizontalLayout implements HasUrlParameter<String> {
    public static final String VIEW_NAME = "Projects";

    private final ProjectDataProvider projectDataProvider;
    private final NewProjectForm newProjectForm;
    private final GridProject gridProject;
    private final EditProject editProject;
    private final Binder<Project> newResearchBinder;

    public MainViewProjects() {
        setWidthFull();
        newProjectForm = new NewProjectForm();
        projectDataProvider = new ProjectDataProvider();
        gridProject = new GridProject(projectDataProvider);
        VerticalLayout layoutNewProject = new VerticalLayout(newProjectForm, gridProject);
        layoutNewProject.setWidth("40%");
        editProject = new EditProject(projectDataProvider);
        editProject.setWidth("60%");
        newResearchBinder = newProjectForm.getBinder();

        add(layoutNewProject, editProject);
        setEventButtonNewProject();
        setEventGridSelection();
    }

    private void setEventButtonNewProject() {
        newProjectForm.getButtonAdd().addClickListener(event -> {
            Project newProject = new Project();
            if (newResearchBinder.writeBeanIfValid(newProject)) {
                newProject.setSecret(HMAC.generateRandomKey());
                projectDataProvider.save(newProject);
                newProjectForm.clear();
                ProjectViewLogic.navigateProject(newProject);
            }
        });
    }

    private void setEventGridSelection() {
        gridProject.asSingleSelect().addValueChangeListener(event -> {
            ProjectViewLogic.navigateProject(event.getValue());
        });
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Long idProject = ProjectViewLogic.enter(parameter);
        Project currentProject = null;
        if (idProject != null) {
            currentProject = projectDataProvider.getProjectById(idProject);
        }
        editProject.setProject(currentProject);
        gridProject.selectRow(currentProject);
    }

    @Autowired
    private void addEventManager(ApplicationEventPublisher publisher) {
        projectDataProvider.setApplicationEventPublisher(publisher);
    }
}
