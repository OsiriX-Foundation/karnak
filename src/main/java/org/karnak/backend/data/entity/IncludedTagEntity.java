package org.karnak.backend.data.entity;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "IncludedTag")
@DiscriminatorValue("IncludedTag")
public class IncludedTagEntity extends TagEntity implements Serializable {

  private static final long serialVersionUID = 6644786515302502293L;

  public IncludedTagEntity() {
  }

  public IncludedTagEntity(String tagValue, ProfileElementEntity profileElementEntity) {
    super(tagValue, profileElementEntity);
  }


}
