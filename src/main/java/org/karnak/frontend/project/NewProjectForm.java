package org.karnak.frontend.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.frontend.forwardnode.ProfileDropDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewProjectForm extends HorizontalLayout {

  private final Binder<ProjectEntity> binder;
  private final Button buttonAdd;
  private final TextField textResearchName;
  private final ProfileDropDown profileDropDown;
  private final TextFieldsBindProject textFieldsBindProject;

  @Autowired
  public NewProjectForm(final ProfileDropDown profileDropDown,
      final TextFieldsBindProject textFieldsBindProject) {
    this.profileDropDown = profileDropDown;
    this.textFieldsBindProject = textFieldsBindProject;

    setWidthFull();
    this.binder = textFieldsBindProject.getBinder();
    this.buttonAdd = new Button("Add");
    this.textResearchName = textFieldsBindProject.getTextResearchName();
    setElements();
    add(this.textResearchName, this.profileDropDown, this.buttonAdd);
    this.binder.removeBinding(textFieldsBindProject.getTextSecret());
    this.binder.bindInstanceFields(this);
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
