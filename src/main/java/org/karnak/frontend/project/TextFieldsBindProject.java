package org.karnak.frontend.project;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.ProjectEntity;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.frontend.forwardnode.ProfileDropDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class TextFieldsBindProject {

    private Binder<ProjectEntity> binder;

    private final TextField textResearchName;
    private final TextField textSecret;
    private final ProfileDropDown profileDropDown;

    @Autowired
    public TextFieldsBindProject(final ProfileDropDown profileDropDown) {
        this.profileDropDown = profileDropDown;
        this.textResearchName = new TextField();
        this.textSecret = new TextField();
    }

    @PostConstruct
    public void init() {
        this.binder = setBinder();
    }

    private Binder<ProjectEntity> setBinder() {
        Binder<ProjectEntity> binder = new BeanValidationBinder<>(ProjectEntity.class);
        binder.forField(textResearchName)
            .withValidator(StringUtils::isNotBlank, "Research name is mandatory")
            .bind(ProjectEntity::getName, ProjectEntity::setName);
        binder.forField(textSecret)
            .withValidator(StringUtils::isNotBlank, "Secret is mandatory")
            .withValidator(HMAC::validateKey, "Secret is not valid")
            .bind(project -> {
                if (project.getSecret() != null) {
                    String hexKey = HMAC.byteToHex(project.getSecret());
                    return HMAC.showHexKey(hexKey);
                }
                return null;
            }, (project, s) -> {
                project.setSecret(HMAC.hexToByte(s.replaceAll("-", "")));
            });
        binder.forField(profileDropDown)
            .withValidator(profilePipe -> profilePipe != null,
                "Choose the de-identification profile\n")
            .bind(ProjectEntity::getProfileEntity, ProjectEntity::setProfileEntity);
        return binder;
    }

    public Binder<ProjectEntity> getBinder() {
        return binder;
    }

    public TextField getTextResearchName() {
        return textResearchName;
    }

    public TextField getTextSecret() {
        return textSecret;
    }
}
