package org.karnak.frontend.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileComponent extends VerticalLayout {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileComponent.class);
  private final ProfilePipeService profilePipeService;
  private final ProfileNameGrid profileNameGrid;
  private final WarningDeleteProfileUsed dialogWarning;
  private final ProfileElementMainView profileElementMainView;
  private ProfileEntity profileEntity;
  private Anchor download;
  private Button deleteButton;

  ProfileComponent(
      ProfilePipeService profilePipeService,
      ProfileNameGrid profileNameGrid,
      ProfileElementMainView profileElementMainView) {
    setSizeFull();
    this.profileElementMainView = profileElementMainView;
    this.profilePipeService = profilePipeService;
    this.profileNameGrid = profileNameGrid;
    dialogWarning = new WarningDeleteProfileUsed();
  }

  public static StreamResource createStreamResource(ProfileEntity profileEntity) {
    try {
      profileEntity
          .getProfileElementEntities()
          .sort(Comparator.comparingInt(ProfileElementEntity::getPosition));
      // https://stackoverflow.com/questions/61506368/formatting-yaml-with-jackson
      ObjectMapper mapper =
          new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

      String strYaml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(profileEntity);
      StreamResource streamResource =
          new StreamResource(
              String.format("%s.yml", profileEntity.getName()).replace(" ", "-"),
              () -> new ByteArrayInputStream(strYaml.getBytes()));
      return streamResource;
    } catch (final Exception e) {
      LOGGER.error("Cannot create the StreamResource for downloading the yaml profile", e);
    }
    return null;
  }

  public void setProfile() {
    removeAll();
    H2 title = new H2("Profile");
    ProfileMetadata name =
        new ProfileMetadata("Name", profileEntity.getName(), profileEntity.getBydefault());
    name.getValidateEditButton()
        .addClickListener(
            event -> {
              profileEntity.setName(name.getValue());
              updatedProfilePipes();
            });

    ProfileMetadata version =
        new ProfileMetadata(
            "Profile version", profileEntity.getVersion(), profileEntity.getBydefault());
    version
        .getValidateEditButton()
        .addClickListener(
            event -> {
              profileEntity.setVersion(version.getValue());
              updatedProfilePipes();
            });

    ProfileMetadata minVersion =
        new ProfileMetadata(
            "Min. version KARNAK required",
            profileEntity.getMinimumkarnakversion(),
            profileEntity.getBydefault());
    minVersion
        .getValidateEditButton()
        .addClickListener(
            event -> {
              profileEntity.setMinimumkarnakversion(minVersion.getValue());
              updatedProfilePipes();
            });

    ProfileMetadata defaultIssuerOfPatientID =
        new ProfileMetadata(
            "Default issuer of PatientID", profileEntity.getDefaultissueropatientid(), false);
    defaultIssuerOfPatientID
        .getValidateEditButton()
        .addClickListener(
            event -> {
              profileEntity.setDefaultissueropatientid(defaultIssuerOfPatientID.getValue());
              updatedProfilePipes();
            });
    createDownloadButton(profileEntity);

    ProfileMasksView profileMasksView = new ProfileMasksView(profileEntity.getMaskEntities());

    if (profileEntity.getBydefault()) {
      add(
          new HorizontalLayout(title, download),
          name,
          version,
          minVersion,
          defaultIssuerOfPatientID,
          profileMasksView);
    } else {
      createDeleteButton(profileEntity);
      add(
          new HorizontalLayout(title, download, deleteButton),
          name,
          version,
          minVersion,
          defaultIssuerOfPatientID,
          profileMasksView);
    }
  }

  private void updatedProfilePipes() {
    profilePipeService.updateProfile(profileEntity);
    profileNameGrid.updatedProfilePipesView();
    final StreamResource profileStreamResource = createStreamResource(profileEntity);
    download.setHref(profileStreamResource);
    createDeleteButton(profileEntity);
  }

  public void setEventValidate(ProfileMetadata metadata) {
    metadata
        .getValidateEditButton()
        .addClickListener(
            event -> {
              profileEntity.setName(metadata.getValue());
            });
  }

  public ProfileEntity getProfile() {
    return profileEntity;
  }

  public void setProfile(ProfileEntity profileEntity) {
    if (profileEntity != null) {
      this.profileEntity = profileEntity;
      setProfile();
    }
  }

  public void createDownloadButton(ProfileEntity profileEntity) {
    final StreamResource profileStreamResource = createStreamResource(profileEntity);
    download = new Anchor(profileStreamResource, "");
    download.getElement().setAttribute("download", true);
    download.add(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));
    download.getStyle().set("margin-top", "30px");
  }

  private void createDeleteButton(ProfileEntity profileEntity) {
    deleteButton = new Button((new Icon(VaadinIcon.TRASH)));
    deleteButton.setWidth("100%");
    deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
    deleteButton.getStyle().set("margin-top", "34px");
    deleteButton.addClickListener(
        buttonClickEvent -> {
          if (profileEntity.getProjectEntities() != null
              && profileEntity.getProjectEntities().size() > 0) {
            dialogWarning.setText(profileEntity);
            dialogWarning.open();
          } else {
            profilePipeService.deleteProfile(profileEntity);
            profileNameGrid.updatedProfilePipesView();
            removeProfileInView();
          }
        });
  }

  public void removeProfileInView() {
    profileElementMainView.removeAll();
    removeAll();
  }
}