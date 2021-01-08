package org.karnak.backend.data.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "IncludedTag")
@DiscriminatorValue("IncludedTag")
public class IncludedTagEntity extends TagEntity {

  public IncludedTagEntity() {
  }

  public IncludedTagEntity(String tagValue, ProfileElementEntity profileElementEntity) {
    super(tagValue, profileElementEntity);
  }
}
