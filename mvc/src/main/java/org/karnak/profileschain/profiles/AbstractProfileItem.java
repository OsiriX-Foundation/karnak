package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.profile.Policy;
import org.karnak.profileschain.action.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProfileItem implements ProfileItem {

    public enum Type {
        BASIC_DICOM(StandardProfile.class, "basic.dicom.profile"),
        KEEP_ALL(KeepAllTags.class, "keep.all.tags"),
        REMOVE_PRIVATE_TAG(PrivateTagsProfile.class, "remove.private.tag"),
        REPLACE_UID(UpdateUIDsProfile.class, "replace.uid"),
        SOP_MIN(SOPProfile.class, "keep.mandatory.sop");

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
    protected final ProfileItem profileParent;
    protected final Map<Integer, Action> tagMap;

    public AbstractProfileItem(String name, String codeName, ProfileItem profileParent) {
        this.name = Objects.requireNonNull(name);
        this.codeName = Objects.requireNonNull(codeName);
        this.profileParent = profileParent;
        this.tagMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCodeName() {
        return codeName;
    }

    public ProfileItem getProfileParent() {
        return profileParent;
    }

    public Action getParentAction(DicomElement dcmElem) {
        if (this.profileParent != null) {
            return this.profileParent.getAction(dcmElem);
        }
        return null;
        // return policy == Policy.WHITELIST ? Action.REMOVE : null;
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
