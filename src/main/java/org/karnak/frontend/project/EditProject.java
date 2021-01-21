package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.service.ProjectService;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.frontend.component.ConfirmDialog;
import org.karnak.frontend.forwardnode.ProfileDropDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class EditProject extends VerticalLayout {

  // Services
  private final ProjectService projectService;
  private final ProfilePipeService profilePipeService;

  private ProfileDropDown profileDropDown;
  private final WarningRemoveProjectUsed dialogWarning;
  private Binder<ProjectEntity> binder;
  private TextField textProjectName;
  private ProjectSecret projectSecret;
  private HorizontalLayout horizontalLayoutButtons;
  private Button buttonUpdate;
  private Button buttonRemove;
  private ProjectEntity projectEntity;

  @Autowired
  public EditProject(final ProjectService projectService,
      final ProfilePipeService profilePipeService) {
    this.projectService = projectService;
    this.profilePipeService = profilePipeService;

    this.dialogWarning = new WarningRemoveProjectUsed();
    setEnabled(false);
    setElements();
    setEventButtonAdd();
    setEventButtonRemove();
    add(this.textProjectName, this.profileDropDown, this.projectSecret,
        this.horizontalLayoutButtons);
  }

  public void setProject(ProjectEntity projectEntity) {
    this.projectEntity = projectEntity;
    if (projectEntity != null) {
      binder.setBean(projectEntity);
      setEnabled(true);
    } else {
      binder.removeBean();
      clear();
      setEnabled(false);
    }
  }

  private void setEventButtonAdd() {
    buttonUpdate.addClickListener(
        event -> {
          if (projectEntity != null && binder.writeBeanIfValid(projectEntity)) {
            if (projectEntity.getDestinationEntities() != null
                && projectEntity.getDestinationEntities().size() > 0) {
              ConfirmDialog dialog =
                  new ConfirmDialog(
                      String.format(
                          "The project %s is used, are you sure you want to updated ?",
                          projectEntity.getName()));
              dialog.addConfirmationListener(
                  componentEvent -> {
                    projectService.update(projectEntity);
                  });
              dialog.open();
            } else {
              projectService.update(projectEntity);
            }
          }
        });
  }

  private void setEventButtonRemove() {
    buttonRemove.addClickListener(
        e -> {
          List<DestinationEntity> destinationEntities = projectEntity.getDestinationEntities();
          if (destinationEntities != null && destinationEntities.size() > 0) {
            dialogWarning.setText(projectEntity);
            dialogWarning.open();

          } else {
            projectService.remove(projectEntity);
            clear();
            setEnabled(false);
          }
        });
  }

  private void setElements() {
    TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
    binder = textFieldsBindProject.getBinder();
    textProjectName = textFieldsBindProject.getTextResearchName();
    profileDropDown = textFieldsBindProject.getProfileDropDown();
    projectSecret = new ProjectSecret(textFieldsBindProject.getTextSecret());
    profileDropDown.setItems(profilePipeService.getAllProfiles());
    profileDropDown.setItemLabelGenerator(ProfileEntity::getName);
    textProjectName.setLabel("Project Name");
    textProjectName.setWidthFull();
    profileDropDown.setLabel("De-identification Profile");
    profileDropDown.setWidthFull();
    buttonUpdate = new Button("Update");
    buttonRemove = new Button("Remove");
    buttonRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
    horizontalLayoutButtons = new HorizontalLayout(buttonUpdate, buttonRemove);
  }

  private void clear() {
    binder.readBean(new ProjectEntity());
  }
}
