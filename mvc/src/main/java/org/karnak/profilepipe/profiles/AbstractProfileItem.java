package org.karnak.profilepipe.profiles;

import org.karnak.data.profile.Argument;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.ActionItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProfileItem implements ProfileItem {

    public enum Type {
        BASIC_DICOM(BasicProfile.class, "basic.dicom.profile"),
        CLEAN_PIXEL_DATA(CleanPixelData.class, "clean.pixel.data"),
        REPLACE_UID(UpdateUIDsProfile.class, "replace.uid"),
        ACTION_TAGS(ActionTags.class, "action.on.specific.tags"),
        ACTION_PRIVATETAGS(PrivateTags.class, "action.on.privatetags"),
        ACTION_DATES(ActionDates.class, "action.on.dates"),
        EXPRESSION_TAGS(Expression.class, "expression.on.tags");

        private final Class<? extends ProfileItem> profileClass;
        private final String classAlias;

        Type(Class<? extends ProfileItem> profileClass, String alias) {
            this.profileClass = profileClass;
            this.classAlias = alias;
        }

        public Class<? extends ProfileItem> getProfileClass() {
            return profileClass;
        }

        public String getClassAlias() {
            return classAlias;
        }

        public static Type getType(String alias) {
            for (Type t : Type.values()) {
                if (t.classAlias.equals(alias)) {
                    return t;
                }
            }
            return null;
        }
    }

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
