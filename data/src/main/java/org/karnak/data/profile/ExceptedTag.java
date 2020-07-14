package org.karnak.data.profile;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ExceptedTag")
@DiscriminatorValue("ExceptedTag")
public class ExceptedTag extends Tag{

    public ExceptedTag() {
    }

    public ExceptedTag(String tagValue, Profile profile) {
        super(tagValue, profile);
    }
}
