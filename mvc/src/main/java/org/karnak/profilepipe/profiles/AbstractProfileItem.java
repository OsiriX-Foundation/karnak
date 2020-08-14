package org.karnak.profilepipe.profiles;

import org.karnak.data.profile.Argument;
import org.karnak.data.profile.ExcludedTag;
import org.karnak.data.profile.IncludedTag;
import org.karnak.data.profile.ProfileElement;
import org.karnak.profilepipe.action.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProfileItem implements ProfileItem {

    public enum Type {
        BASIC_DICOM(BasicProfile.class, "basic.dicom.profile"),
        REPLACE_UID(UpdateUIDsProfile.class, "replace.uid"),
        ACTION_TAGS(ActionTags.class, "action.on.specific.tags"),
        ACTION_PRIVATETAGS(PrivateTags.class, "action.on.privatetags"),
        ACTION_DATES(ActionDates.class, "action.on.dates");

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
    protected final Map<Integer, Action> tagMap;
    protected final Integer position;

    public AbstractProfileItem(ProfileElement profileElement) {
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
    public Action remove(int tag) {
        return tagMap.remove(tag);
    }

    @Override
    public Action put(int tag, Action action) {
        Objects.requireNonNull(action);
        /*
        Garde fou
        if ((policy == Policy.WHITELIST && action != Action.KEEP && action != Action.DEFAULT_DUMMY) || (policy == Policy.BLACKLIST && action == Action.KEEP)) {
            throw new IllegalStateException(String.format("The action %s is not consistent with the profile policy %s!", action, policy));
        }
        */
        return tagMap.put(tag, action);
    }
}
