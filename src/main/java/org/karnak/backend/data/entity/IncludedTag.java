package org.karnak.backend.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "IncludedTag")
@DiscriminatorValue("IncludedTag")
public class IncludedTag extends Tag {

  public IncludedTag() {
  }

  public IncludedTag(String tagValue, ProfileElement profileElement) {
    super(tagValue, profileElement);
  }
}
