package org.karnak.frontend.profile;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;
import org.karnak.frontend.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("KARNAK - Profiles")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class ProfileView extends HorizontalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileView.class);
    public static final String VIEW_NAME = "Profiles";

    private final ProfileComponent profileComponent;
    private final ProfileElementMainView profileElementMainView;
    private Upload uploadProfile;
    private final ProfileNameGrid profileNameGrid;
    private final ProfileErrorView profileErrorView;
    private final ProfilePipeService profilePipeService;
    private final HorizontalLayout profileHorizontalLayout;

    public ProfileView() {
        setSizeFull();
        profilePipeService = new ProfilePipeServiceImpl();
        profileNameGrid = new ProfileNameGrid();
        profileElementMainView = new ProfileElementMainView();
        profileComponent = new ProfileComponent(profilePipeService, profileNameGrid, profileElementMainView);
        profileErrorView = new ProfileErrorView();
        profileHorizontalLayout = new HorizontalLayout(profileComponent, profileElementMainView);

        profileComponent.setWidth("45%");
        profileElementMainView.setWidth("55%");
        profileHorizontalLayout.getStyle().set("overflow-y", "auto");
        profileHorizontalLayout.setWidth("75%");

        profileErrorView.setWidth("75%");


        VerticalLayout barAndGridLayout = createTopLayoutGrid();
        barAndGridLayout.setWidth("25%");
        add(barAndGridLayout);
    }

    private VerticalLayout createTopLayoutGrid() {
        HorizontalLayout topLayout = createTopBar();
        SingleSelect<Grid<ProfileEntity>, ProfileEntity> profilePipeSingleSelect =
            profileNameGrid.asSingleSelect();

        profilePipeSingleSelect.addValueChangeListener(e -> {
            ProfileEntity profileEntitySelected = e.getValue();
            if (profileEntitySelected != null) {
                profileComponent.setProfile(profileEntitySelected);
                profileElementMainView
                    .setProfiles(profileEntitySelected.getProfileElementEntities());
                remove(profileErrorView);
                add(profileHorizontalLayout);
            }
        });

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(profileNameGrid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setFlexGrow(1, profileNameGrid);
        barAndGridLayout.setSizeFull();
        return barAndGridLayout;
    }

    private HorizontalLayout createTopBar() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        // https://github.com/vaadin/vaadin-upload-flow/blob/6fa9cc429e1d0894704fb962e0df375a9d0439c8/vaadin-upload-flow-integration-tests/src/main/java/com/vaadin/flow/component/upload/tests/it/UploadView.java#L122
        uploadProfile = new Upload(memoryBuffer);
        uploadProfile.setDropLabel(new Span("Drag and drop your profile here"));
        uploadProfile.addSucceededListener(e -> {
            setProfileComponent(e.getMIMEType(), memoryBuffer.getInputStream());
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(uploadProfile);
        return layout;
    }

    private void setProfileComponent(String mimeType, InputStream stream) {
        remove(profileHorizontalLayout);
        add(profileErrorView);
        try {
            ProfilePipeBody profilePipe = readProfileYaml(stream);
            ArrayList<ProfileError> profileErrors = profilePipeService.validateProfile(profilePipe);
            Predicate<ProfileError> errorPredicate = profileError -> profileError.getError() != null;
            if (!profileErrors.stream().anyMatch(errorPredicate)) {
                remove(profileErrorView);
                ProfileEntity newProfileEntity = profilePipeService
                    .saveProfilePipe(profilePipe, false);
                profileNameGrid.updatedProfilePipesView();
                profileNameGrid.selectRow(newProfileEntity);
            } else {
                profileErrorView.setView(profileErrors);
            }
        } catch (YAMLException e) {
            LOGGER.error("Unable to read uploaded YAML" ,  e);
            profileErrorView.setView("Unable to read uploaded YAML file.\n" +
                    "Please make sure it is a YAML file and respects the YAML structure.");
        }
    }

    private ProfilePipeBody readProfileYaml(InputStream stream) {
        final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
        return yaml.load(stream);
    }
}
