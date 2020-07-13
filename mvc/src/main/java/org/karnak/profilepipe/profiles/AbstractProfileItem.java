package org.karnak.profilepipe.profiles;

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
        ACTION_PRIVATETAGS(PrivateTags.class, "action.on.privatetags");

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
    protected final String action;
    protected final List<String> tags;
    protected final List<String> exceptedTags;
    protected final Map<Integer, Action> tagMap;

    public AbstractProfileItem(String name, String codeName, String action, List<String> tags, List<String> exceptedTags) {
        this.name = Objects.requireNonNull(name);
        this.codeName = Objects.requireNonNull(codeName);
        this.action = action;
        this.tags = tags;
        this.exceptedTags = exceptedTags;
        this.tagMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCodeName() {
        return codeName;
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
