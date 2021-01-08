package org.karnak.backend.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ExcludedTag")
@DiscriminatorValue("ExcludedTag")
public class ExcludedTagEntity extends TagEntity {

  public ExcludedTagEntity() {
  }

  public ExcludedTagEntity(String tagValue, ProfileElementEntity profileElementEntity) {
    super(tagValue, profileElementEntity);
  }
}
