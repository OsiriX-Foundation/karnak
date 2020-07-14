package org.karnak.data.profile;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "IncludedTag")
@DiscriminatorValue("IncludedTag")
public class IncludedTag extends Tag{

    public IncludedTag() {
    }

    public IncludedTag(String tagValue, Profile profile) {
        super(tagValue, profile);
    }
}
