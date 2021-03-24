package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.karnak.backend.enums.ExternalIDType;

@Entity(name = "ExternalIDProvider")
@Table(name = "externalid_provider")
public class ExternalIDProviderEntity {

  private static final long serialVersionUID = 158546565156165167L;

  private Long id;
  private boolean bydefault;
  private ExternalIDType type;
  private String filePath;
  private String classPath;

  public ExternalIDProviderEntity() {}

  public ExternalIDProviderEntity(
      boolean bydefault, ExternalIDType type, String filePath, String classPath) {
    this.bydefault = bydefault;
    this.type = type;
    this.filePath = filePath;
    this.classPath = classPath;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isBydefault() {
    return bydefault;
  }

  public void setBydefault(boolean bydefault) {
    this.bydefault = bydefault;
  }

  public ExternalIDType getType() {
    return type;
  }

  public void setType(ExternalIDType type) {
    this.type = type;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getClassPath() {
    return classPath;
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

}
