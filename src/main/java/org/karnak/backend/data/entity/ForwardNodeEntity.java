package org.karnak.backend.data.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "ForwardNode")
@Table(name = "forward_node")
public class ForwardNodeEntity {

  // Specification of a DICOM source node (the one which sends images to the
  // gateway). When no source node is defined all the DICOM nodes are accepted by
  // the gateway.
  @OneToMany(
      mappedBy = "forwardNodeEntity",
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  private final Set<DicomSourceNodeEntity> sourceNodes = new HashSet<>();
  // Specification of a final DICOM destination node. Multiple destinations can be
  // defined either as a DICOM or DICOMWeb type.
  @OneToMany(
      mappedBy = "forwardNodeEntity",
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  private final Set<DestinationEntity> destinationEntities = new HashSet<>();
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String description;
  // AETitle which defined a mapping of the gateway. This AETitle is configured as
  // a destination in the DICOM component that sends images to the gateway.
  @NotBlank(message = "Forward AETitle is mandatory")
  @Size(max = 16, message = "Forward AETitle has more than 16 characters")
  private String fwdAeTitle;

  protected ForwardNodeEntity() {
    this.fwdAeTitle = "";
    this.description = "";
  }

  public ForwardNodeEntity(String fwdAeTitle) {
    this.fwdAeTitle = fwdAeTitle;
    this.description = "";
  }

  public static ForwardNodeEntity ofEmpty() {
    ForwardNodeEntity instance = new ForwardNodeEntity();
    return instance;
  }

  public Long getId() {
    return id;
  }

  public boolean isNewData() {
    return id == null;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFwdAeTitle() {
    return this.fwdAeTitle;
  }

  public void setFwdAeTitle(String fwdAeTitle) {
    this.fwdAeTitle = fwdAeTitle;
  }

  public Set<DicomSourceNodeEntity> getSourceNodes() {
    return this.sourceNodes;
  }

  public void addSourceNode(DicomSourceNodeEntity sourceNode) {
    sourceNode.setForwardNodeEntity(this);
    this.sourceNodes.add(sourceNode);
  }

  public void removeSourceNode(DicomSourceNodeEntity sourceNode) {
    if (this.sourceNodes.remove(sourceNode)) {
      sourceNode.setForwardNodeEntity(null);
    }
  }

  public Set<DestinationEntity> getDestinationEntities() {
    return destinationEntities;
  }

  public void addDestination(DestinationEntity destinationEntity) {
    destinationEntity.setForwardNodeEntity(this);
    this.destinationEntities.add(destinationEntity);
  }

  public void removeDestination(DestinationEntity destinationEntity) {
    if (this.destinationEntities.remove(destinationEntity)) {
      destinationEntity.setForwardNodeEntity(null);
    }
  }

  /**
   * Informs if this object matches with the filter as text.
   *
   * @param filterText the filter as text.
   * @return true if this object matches with the filter as text; false otherwise.
   */
  public boolean matchesFilter(String filterText) {
    if (contains(fwdAeTitle, filterText) //
        || contains(description, filterText)) {
      return true;
    }

    for (DicomSourceNodeEntity sourceNode : sourceNodes) {
      if (sourceNode.matchesFilter(filterText)) {
        return true;
      }
    }

    for (DestinationEntity destinationEntity : destinationEntities) {
      if (destinationEntity.matchesFilter(filterText)) {
        return true;
      }
    }

    return false;
  }

  private boolean contains(String value, String filterText) {
    return value != null && value.contains(filterText);
  }

  @Override
  public String toString() {
    return "ForwardNode [id="
        + id
        + ", description="
        + description
        + ", fwdAeTitle="
        + fwdAeTitle
        + ", sourceNodes="
        + sourceNodes
        + ", destinations="
        + destinationEntities
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ForwardNodeEntity other = (ForwardNodeEntity) obj;
    if (id == null) {
      return other.id == null;
    } else {
      return id.equals(other.id);
    }
  }
}