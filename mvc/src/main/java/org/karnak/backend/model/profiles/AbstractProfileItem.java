package org.karnak.backend.model.profiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.data.profile.Argument;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;

public abstract class AbstractProfileItem implements ProfileItem {

    protected final String name;
    protected final String codeName;
    protected final String condition;
    protected final String action;
    protected final String option;
    protected final List<Argument> arguments;
    protected final List<IncludedTag> tags;
    protected final List<ExcludedTag> excludedTags;
    protected final Map<Integer, ActionItem> tagMap;
    protected final Integer position;

    protected AbstractProfileItem(ProfileElement profileElement) {
        this.name = Objects.requireNonNull(profileElement.getName());
        this.codeName = Objects.requireNonNull(profileElement.getCodename());
        this.condition = profileElement.getCondition();
        this.action = profileElement.getAction();
        this.option = profileElement.getOption();
        this.arguments = profileElement.getArguments();
        this.tags = profileElement.getIncludedtag();
        this.excludedTags = profileElement.getExceptedtags();
        this.position = profileElement.getPosition();
        this.tagMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getCondition() { return condition; }

    public String getOption() { return option; }

    public List<Argument> getArguments() { return arguments; }

    public Integer getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void clearTagMap() {
        tagMap.clear();
    }

    @Override
    public ActionItem remove(int tag) {
        return tagMap.remove(tag);
    }

    @Override
    public ActionItem put(int tag, ActionItem action) {
        Objects.requireNonNull(action);
        return tagMap.put(tag, action);
    }

    @Override
    public void profileValidation() throws Exception {}
}
