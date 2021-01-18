package org.karnak.backend.data.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "KheopsAlbums")
@Table(name = "kheops_albums")
public class KheopsAlbumsEntity implements Serializable {

  private static final long serialVersionUID = -3315720301354286325L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String urlAPI;
  private String authorizationDestination;
  private String authorizationSource;
  private String condition;

  @ManyToOne
  @JoinColumn(name = "destination_id")
  private DestinationEntity destinationEntity = new DestinationEntity();

  public KheopsAlbumsEntity() {
  }

  public KheopsAlbumsEntity(String urlAPI, String authorizationDestination,
      String authorizationSource, String condition) {
    this.urlAPI = urlAPI;
    this.authorizationDestination = authorizationDestination;
    this.authorizationSource = authorizationSource;
    this.condition = condition;
  }

    public Long getId() {
        return id;
    }

    public String getUrlAPI() {
        return urlAPI;
    }

    public void setUrlAPI(String urlAPI) {
        this.urlAPI = urlAPI;
    }

    public String getAuthorizationDestination() {
        return authorizationDestination;
    }

    public void setAuthorizationDestination(String authorizationDestination) {
        this.authorizationDestination = authorizationDestination;
    }

    public String getAuthorizationSource() {
        return authorizationSource;
    }

    public void setAuthorizationSource(String authorizationSource) {
        this.authorizationSource = authorizationSource;
    }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public DestinationEntity getDestinationEntity() {
    return destinationEntity;
  }

  public void setDestinationEntity(DestinationEntity destinationEntity) {
    this.destinationEntity = destinationEntity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KheopsAlbumsEntity that = (KheopsAlbumsEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(urlAPI, that.urlAPI) &&
        Objects.equals(authorizationDestination, that.authorizationDestination) &&
        Objects.equals(authorizationSource, that.authorizationSource) &&
        Objects.equals(condition, that.condition) &&
        Objects.equals(destinationEntity, that.destinationEntity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, urlAPI, authorizationDestination, authorizationSource, condition,
        destinationEntity);
  }
}
