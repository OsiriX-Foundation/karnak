package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import java.io.Serial;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.component.ProjectDropDown;
import org.karnak.frontend.util.UIS;

public class TagMorphingComponent extends VerticalLayout {

  @Serial
  private static final long serialVersionUID = 6526643405482005449L;

  // Labels
  private static final String LABEL_CHECKBOX_TAG_MORPHING = "Activate tag morphing";

  // Components
  private Checkbox tagMorphingCheckbox;

  private ProjectDropDown projectDropDown;

  private WarningNoProjectsDefined warningNoProjectsDefined;

  private Binder<DestinationEntity> destinationBinder;

  private Div tagMorphingDiv;

  private final DestinationComponentUtil destinationComponentUtil;

  private ProfileLabel profileLabel;

  /** Constructor */
  public TagMorphingComponent() {
    this.destinationComponentUtil = new DestinationComponentUtil();
  }

  /**
   * Init deidentification component
   *
   * @param binder Binder for checks
   */
  public void init(final Binder<DestinationEntity> binder) {
    // Init destination binder
    setDestinationBinder(binder);

    // Build deidentification components
    buildComponents();

    // Init destination binder
    initDestinationBinder();

    // Build Listeners
    buildListeners();

    // Add components
    addComponents();
  }

  public Checkbox getTagMorphingCheckbox() {
    return tagMorphingCheckbox;
  }

  public void setTagMorphingCheckbox(Checkbox tagMorphingCheckbox) {
    this.tagMorphingCheckbox = tagMorphingCheckbox;
  }

  public ProjectDropDown getProjectDropDown() {
    return projectDropDown;
  }

  public void setProjectDropDown(ProjectDropDown projectDropDown) {
    this.projectDropDown = projectDropDown;
  }

  public WarningNoProjectsDefined getWarningNoProjectsDefined() {
    return warningNoProjectsDefined;
  }

  public void setWarningNoProjectsDefined(
      WarningNoProjectsDefined warningNoProjectsDefined) {
    this.warningNoProjectsDefined = warningNoProjectsDefined;
  }

  public Binder<DestinationEntity> getDestinationBinder() {
    return destinationBinder;
  }

  public void setDestinationBinder(
      Binder<DestinationEntity> destinationBinder) {
    this.destinationBinder = destinationBinder;
  }

  /** Build deidentification components */
  private void buildComponents() {
    profileLabel = new ProfileLabel();
    projectDropDown = destinationComponentUtil.buildProjectDropDown();
    warningNoProjectsDefined = destinationComponentUtil.buildWarningNoProjectDefined();
    tagMorphingCheckbox = destinationComponentUtil.buildActivateCheckbox(LABEL_CHECKBOX_TAG_MORPHING);
    tagMorphingDiv = destinationComponentUtil.buildActivateDiv();
  }

  private void initDestinationBinder() {
    destinationBinder
        .forField(tagMorphingCheckbox)
        .bind(DestinationEntity::isActivateTagMorphing, DestinationEntity::setActivateTagMorphing);
    destinationBinder
        .forField(projectDropDown)
        .withValidator(
            project -> project != null || !tagMorphingCheckbox.getValue(),
            "Choose a project")
        .bind(DestinationEntity::getTagMorphingProjectEntity, DestinationEntity::setTagMorphingProjectEntity);
  }

  /** Build listeners */
  private void buildListeners() {
    destinationComponentUtil.buildWarningNoProjectDefinedListener(warningNoProjectsDefined,
        tagMorphingCheckbox);
    destinationComponentUtil.buildProjectDropDownListener(projectDropDown, profileLabel);
  }

  /** Add components */
  private void addComponents() {
    // Padding
    setPadding(true);

    // Add components in div
    tagMorphingDiv.add(
        projectDropDown,
        profileLabel);

    // If checkbox is checked set div visible, invisible otherwise
    tagMorphingDiv.setVisible(tagMorphingCheckbox.getValue());

    // Add components in view
    add(UIS.setWidthFull(new HorizontalLayout(tagMorphingCheckbox, tagMorphingDiv)));
  }

  public Div getTagMorphingDiv() {
    return tagMorphingDiv;
  }

  public DestinationComponentUtil getDestinationComponentUtil() {
    return destinationComponentUtil;
  }

  public ProfileLabel getProfileLabel() {
    return profileLabel;
  }
}
