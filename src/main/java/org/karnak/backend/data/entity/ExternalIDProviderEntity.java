package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.karnak.backend.enums.ExternalIDProviderType;

@Entity(name = "ExternalIDProvider")
@Table(name = "externalid_provider")
public class ExternalIDProviderEntity {

  private static final long serialVersionUID = 158546565156165167L;

  private Long id;
  private boolean bydefault;
  private ExternalIDProviderType externalIDProviderType;
  private String filePath;
  private String classPath;

  public ExternalIDProviderEntity() {}

  public ExternalIDProviderEntity(
      boolean bydefault, ExternalIDProviderType externalIDProviderType, String filePath, String classPath) {
    this.bydefault = bydefault;
    this.externalIDProviderType = externalIDProviderType;
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

  @Column(name = "externalid_provider_type")
  @Enumerated(EnumType.STRING)
  public ExternalIDProviderType getExternalIDProviderType() {
    return externalIDProviderType;
  }

  public void setExternalIDProviderType(
      ExternalIDProviderType externalIDProviderType) {
    this.externalIDProviderType = externalIDProviderType;
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
