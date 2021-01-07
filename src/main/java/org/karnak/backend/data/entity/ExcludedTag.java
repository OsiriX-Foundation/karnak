package org.karnak.backend.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ExcludedTag")
@DiscriminatorValue("ExcludedTag")
public class ExcludedTag extends Tag {

  public ExcludedTag() {
  }

  public ExcludedTag(String tagValue, ProfileElement profileElement) {
    super(tagValue, profileElement);
  }
}
