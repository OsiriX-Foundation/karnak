package org.karnak.ui.profile;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;
import org.karnak.ui.MainLayout;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile configuration")
public class ProfileView extends HorizontalLayout {
    public static final String VIEW_NAME = "Profile";
    private Div profileOutput = new Div();

    public ProfileView() {
        setSizeFull();
        VerticalLayout topLayout = createTopBar();
        /*
        HorizontalLayout profileData = new HorizontalLayout();
        profileData.add(profileOutput);
        */

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout, profileOutput);
        add(barAndGridLayout);
    }

    private VerticalLayout createTopBar() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        // https://github.com/vaadin/vaadin-upload-flow/blob/6fa9cc429e1d0894704fb962e0df375a9d0439c8/vaadin-upload-flow-integration-tests/src/main/java/com/vaadin/flow/component/upload/tests/it/UploadView.java#L122
        Upload uploadProfile = new Upload(memoryBuffer);
        uploadProfile.addSucceededListener(e -> {
            Component component = createComponent(e.getMIMEType(), memoryBuffer.getInputStream());
            showProfile(e.getFileName(), component, profileOutput);
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(uploadProfile);
        return layout;
    }

    private Component createComponent(String mimeType,
                                      InputStream stream) {
        if (mimeType.equals("application/x-yaml")) {
            return createProfileComponent(stream);
        }
        Div content = new Div();
        String text = String.format("Mime type: '%s'\nMust be 'application/x-yaml'",
                mimeType);
        content.setText(text);
        return content;
    }

    private Component createProfileComponent(InputStream stream) {
        ProfilePipeBody profilePipe = readProfileYaml(stream);
        ProfileComponent profileComponent = new ProfileComponent(profilePipe);
        return profileComponent.getComponent();
    }

    private ProfilePipeBody readProfileYaml(InputStream stream) {
        final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
        return yaml.load(stream);
    }

    private void showProfile(String text, Component content,
                            HasComponents outputContainer) {
        outputContainer.removeAll();
        outputContainer.add(content);
    }
}
