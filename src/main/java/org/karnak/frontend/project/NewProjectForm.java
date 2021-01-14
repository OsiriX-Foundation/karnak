package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.frontend.forwardnode.ProfileDropDown;

public class NewProjectForm extends HorizontalLayout {

  private final Binder<ProjectEntity> binder;

  private final Button buttonAdd;
  private final TextField textResearchName;
  private final ProfileDropDown profileDropDown;

  public NewProjectForm() {
    setWidthFull();
    TextFieldsBindProject textFieldsBindProject = new TextFieldsBindProject();
    binder = textFieldsBindProject.getBinder();
    buttonAdd = new Button("Add");
    textResearchName = textFieldsBindProject.getTextResearchName();
    profileDropDown = textFieldsBindProject.getProfileDropDown();
    setElements();

    add(textResearchName, profileDropDown, buttonAdd);
    binder.removeBinding(textFieldsBindProject.getTextSecret());
    binder.bindInstanceFields(this);
  }

  private void setElements() {
    textResearchName.setWidth("20%");
    textResearchName.getStyle().set("padding-right", "10px");
    textResearchName.setPlaceholder("Research Name");
  }

  public Button getButtonAdd() {
    return buttonAdd;
  }

  public void clear() {
    binder.readBean(new ProjectEntity());
  }

  public Binder<ProjectEntity> getBinder() {
    return binder;
  }
}
