package org.karnak.backend.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.entity.SOPClassUIDEntity;

/**
 * Back-end service interface for retrieving and updating data.
 */

public abstract class DataService implements Serializable {

  private static final long serialVersionUID = -1402338736361739563L;

  public abstract Collection<ForwardNodeEntity> getAllForwardNodes();

  public abstract ForwardNodeEntity getForwardNodeById(Long dataId);

  public abstract ForwardNodeEntity updateForwardNode(ForwardNodeEntity data);

  public abstract void deleteForwardNode(Long dataId);

  public abstract Collection<DestinationEntity> getAllDestinations(
      ForwardNodeEntity forwardNodeEntity);

  public abstract DestinationEntity getDestinationById(ForwardNodeEntity forwardNodeEntity,
      Long dataId);

  public abstract DestinationEntity updateDestination(ForwardNodeEntity forwardNodeEntity,
      DestinationEntity data);

  public abstract void deleteDestination(ForwardNodeEntity forwardNodeEntity,
      DestinationEntity data);

  public abstract Collection<DicomSourceNodeEntity> getAllSourceNodes(
      ForwardNodeEntity forwardNodeEntity);

  public abstract DicomSourceNodeEntity getSourceNodeById(ForwardNodeEntity forwardNodeEntity,
      Long dataId);

  public abstract DicomSourceNodeEntity updateSourceNode(ForwardNodeEntity forwardNodeEntity,
      DicomSourceNodeEntity data);

  public abstract void deleteSourceNode(ForwardNodeEntity forwardNodeEntity,
      DicomSourceNodeEntity data);

  public abstract List<SOPClassUIDEntity> getAllSOPClassUIDs();

  public abstract SOPClassUIDEntity getSOPClassUIDByName(String name);

  public abstract SOPClassUIDEntity getSOPClassUIDById(Long dataId);
}
