package org.karnak.profileschain.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProfileItem implements ProfileItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProfileItem.class);

    public enum Type {
        BASIC_DICOM(StandardProfile.class, "basic.dicom.profile"),
        REMOVE_OVERLAY(OverlaysProfile.class, "remove.overlay"),
        REMOVE_PRIVATE_TAG(PrivateTagsProfile.class, "remove.private.tag"),
        REPLACE_UID(UpdateUIDsProfile.class, "replace.uid"),
        SOP_MIN(SOPProfile.class, "keep.mandatory.sop");

        private final Class<? extends ProfileItem> profileClass;
        private final String classAlias;

        private Type(Class<? extends ProfileItem> profileClass, String alias) {
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

    public AbstractProfileItem(String name, String codeName) {
        this.name = name;
        this.codeName = codeName;
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

}
