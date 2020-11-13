package org.karnak.ui.profile;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.profile.Profile;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;
import org.karnak.ui.MainLayout;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Predicate;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("KARNAK - Profiles")
@SuppressWarnings("serial")
public class ProfileView extends HorizontalLayout {
    public static final String VIEW_NAME = "Profiles";

    private ProfileComponent profileComponent;
    private ProfileElementMainView profileElementMainView;
    private Upload uploadProfile;
    private ProfileNameGrid profileNameGrid;
    private ProfileErrorView profileErrorView;
    private final ProfilePipeService profilePipeService;

    public ProfileView() {
        profilePipeService = new ProfilePipeServiceImpl();
        profileNameGrid = new ProfileNameGrid();
        profileComponent = new ProfileComponent(profilePipeService, profileNameGrid);
        profileElementMainView = new ProfileElementMainView();
        profileErrorView = new ProfileErrorView();
        setSizeFull();
        VerticalLayout barAndGridLayout = createTopLayoutGrid();
        barAndGridLayout.setWidth("25%");
        profileComponent.setWidth("30%");
        profileElementMainView.setWidth("45%");
        profileErrorView.setWidth("75%");
        add(barAndGridLayout);
    }

    private VerticalLayout createTopLayoutGrid() {
        HorizontalLayout topLayout = createTopBar();
        SingleSelect<Grid<Profile>, Profile> profilePipeSingleSelect =
                profileNameGrid.asSingleSelect();

        profilePipeSingleSelect.addValueChangeListener(e -> {
            Profile profileSelected = e.getValue();
            if (profileSelected != null) {
                profileComponent.setProfile(profileSelected);
                profileElementMainView.setProfiles(profileSelected.getProfileElements());
                remove(profileErrorView);
                add(profileComponent);
                add(profileElementMainView);
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
        remove(profileComponent);
        remove(profileElementMainView);
        add(profileErrorView);
        if (mimeType.equals("application/x-yaml")) {
            ProfilePipeBody profilePipe = readProfileYaml(stream);
            ArrayList<ProfileError> profileErrors = profilePipeService.validateProfile(profilePipe);
            Predicate<ProfileError> errorPredicate = profileError -> profileError.getError() != null;
            if (!profileErrors.stream().anyMatch(errorPredicate)) {
                remove(profileErrorView);
                Profile newProfile = profilePipeService.saveProfilePipe(profilePipe, false);
                profileNameGrid.updatedProfilePipesView();
                profileNameGrid.selectRow(newProfile);
            } else {
                profileErrorView.setView(profileErrors);
            }
        } else {
            profileErrorView.setView("mimeType must be 'application/x-yaml'");
        }
    }

    private ProfilePipeBody readProfileYaml(InputStream stream) {
        final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
        return yaml.load(stream);
    }
}
