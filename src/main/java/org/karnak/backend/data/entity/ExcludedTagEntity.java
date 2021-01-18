package org.karnak.backend.data.entity;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ExcludedTag")
@DiscriminatorValue("ExcludedTag")
public class ExcludedTagEntity extends TagEntity implements Serializable {

  private static final long serialVersionUID = -5068272710332856139L;

  public ExcludedTagEntity() {
  }

  public ExcludedTagEntity(String tagValue, ProfileElementEntity profileElementEntity) {
    super(tagValue, profileElementEntity);
  }

}
