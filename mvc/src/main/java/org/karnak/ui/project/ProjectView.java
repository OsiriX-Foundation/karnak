package org.karnak.ui.project;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.gateway.Project;
import org.karnak.ui.MainLayout;

@Route(value = "project", layout = MainLayout.class)
@PageTitle("KARNAK - Project")
public class ProjectView extends VerticalLayout {
    public static final String VIEW_NAME = "Project";
    private NewProjectForm newProjectForm;
    private GridProject gridProject;
    private Binder<Project> newResearchBinder;
    private ListDataProvider<Project> dataProviderResearch;

    public ProjectView() {
        newProjectForm = new NewProjectForm();
        gridProject = new GridProject();
        dataProviderResearch = (ListDataProvider<Project>) gridProject.getDataProvider();
        newResearchBinder = newProjectForm.getBinder();
        add(newProjectForm, gridProject);
        setEventButtonAdd();
    }

    private void setEventButtonAdd() {
        newProjectForm.getButtonAdd().addClickListener(event -> {
            Project newProject = new Project();
            if (newResearchBinder.writeBeanIfValid(newProject)) {
                dataProviderResearch.getItems().add(newProject);
                dataProviderResearch.refreshAll();
                newProjectForm.clear();
            }
        });
    }
}
