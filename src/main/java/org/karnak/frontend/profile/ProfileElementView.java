package org.karnak.frontend.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.TagEntity;

public class ProfileElementView extends Div {

    ProfileElementEntity profileElementEntity;

    public ProfileElementView(ProfileElementEntity profileElementEntity) {
        this.profileElementEntity = profileElementEntity;
        getStyle().set("margin-top", "0px");
        setView();
    }

    public void setView() {
        removeAll();
        if (profileElementEntity.getCodename() != null) {
            add(setProfileValue("Codename : " + profileElementEntity.getCodename()));
        }
        if (profileElementEntity.getAction() != null) {
            add(setProfileValue("Action : " + profileElementEntity.getAction()));
        }
        if (profileElementEntity.getOption() != null) {
            add(setProfileValue("Option : " + profileElementEntity.getOption()));
        }
        if (profileElementEntity.getArgumentEntities() != null
            && profileElementEntity.getArgumentEntities().size() > 0) {
            add(setProfileValue("Arguments"));
            add(setProfileArguments(profileElementEntity.getArgumentEntities()));
        }
        if (profileElementEntity.getCondition() != null) {
            add(setProfileValue("Condition : " + profileElementEntity.getCondition()));
        }
        if (profileElementEntity.getIncludedTagEntities().size() > 0) {
            add(setProfileValue("Tags"));
            add(setProfileTags(profileElementEntity.getIncludedTagEntities()));
        }
        if (profileElementEntity.getExcludedTagEntities().size() > 0) {
            add(setProfileValue("Excluded tags"));
            add(setProfileTags(profileElementEntity.getExcludedTagEntities()));
        }
    }

    private Div setProfileValue(String value) {
        Div profileNameDiv = new Div();
        profileNameDiv.add(new Text(value));
        profileNameDiv.getStyle().set("color", "grey").set("padding-left", "10px").set("margin-top", "5px");
        return profileNameDiv;
    }

    private VerticalLayout setProfileTags(List<? extends TagEntity> tagEntities) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("margin-top", "0px");
        for (TagEntity tagEntity : tagEntities) {
            Div tagDiv = new Div();
            tagDiv.add(new Text(tagEntity.getTagValue()));
            tagDiv.getStyle().set("color", "grey").set("padding-left", "15px")
                .set("margin-top", "2px");
            verticalLayout.add(tagDiv);
        }
        return verticalLayout;
    }

    private VerticalLayout setProfileArguments(List<ArgumentEntity> argumentEntities) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("margin-top", "0px");
        for (ArgumentEntity argumentEntity : argumentEntities) {
            Div tagDiv = new Div();
            tagDiv.add(new Text(argumentEntity.getKey() + " : " + argumentEntity.getValue()));
            tagDiv.getStyle().set("color", "grey").set("padding-left", "15px")
                .set("margin-top", "2px");
            verticalLayout.add(tagDiv);
        }
        return verticalLayout;
    }
}
