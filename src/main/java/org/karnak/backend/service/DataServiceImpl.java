package org.karnak.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.ForwardNodeRepo;
import org.karnak.backend.data.repo.SOPClassUIDRepo;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class DataServiceImpl extends DataService {

  private final ForwardNodeRepo forwardNodeRepo;
  private final SOPClassUIDRepo sopClassUIDRepo;

  {
    forwardNodeRepo = GatewayConfig.getInstance().getGatewayPersistence();
  }

  {
    sopClassUIDRepo = GatewayConfig.getInstance().getSopClassUIDPersistence();
  }


  @Override
  public Collection<ForwardNodeEntity> getAllForwardNodes() {
    List<ForwardNodeEntity> list = new ArrayList<>();
    forwardNodeRepo.findAll() //
        .forEach(list::add);
    return list;
  }

  @Override
  public ForwardNodeEntity getForwardNodeById(Long dataId) {
    return forwardNodeRepo.findById(dataId).orElse(null);
  }

  @Override
  public ForwardNodeEntity updateForwardNode(ForwardNodeEntity data) {
    return forwardNodeRepo.saveAndFlush(data);
  }

    @Override
    public void deleteForwardNode(Long dataId) {
      forwardNodeRepo.deleteById(dataId);
      forwardNodeRepo.flush();
    }

  @Override
  public Collection<DestinationEntity> getAllDestinations(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null) {
      return forwardNodeEntity.getDestinationEntities();
    }
    return new HashSet<>();
  }

  @Override
  public DestinationEntity getDestinationById(ForwardNodeEntity forwardNodeEntity, Long dataId) {
    Collection<DestinationEntity> destinationEntities = getAllDestinations(forwardNodeEntity);
    for (DestinationEntity destinationEntity : destinationEntities) {
      if (Objects.equals(destinationEntity.getId(), dataId)) {
        return destinationEntity;
      }
    }
    return null;
  }

  @Override
  public DestinationEntity updateDestination(ForwardNodeEntity forwardNodeEntity,
      DestinationEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return null;
    }
    Collection<DestinationEntity> destinationEntities = getAllDestinations(forwardNodeEntity);
    if (!destinationEntities.contains(data)) {
      forwardNodeEntity.addDestination(data);
    }
    return data;
  }

  @Override
  public void deleteDestination(ForwardNodeEntity forwardNodeEntity, DestinationEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return;
    }
    forwardNodeEntity.removeDestination(data);
  }

  @Override
  public Collection<DicomSourceNodeEntity> getAllSourceNodes(ForwardNodeEntity forwardNodeEntity) {
    if (forwardNodeEntity != null) {
      return forwardNodeEntity.getSourceNodes();
    }
    return new HashSet<>();
  }

  @Override
  public DicomSourceNodeEntity getSourceNodeById(ForwardNodeEntity forwardNodeEntity, Long dataId) {
    Collection<DicomSourceNodeEntity> sourceNodes = getAllSourceNodes(forwardNodeEntity);
    for (DicomSourceNodeEntity sourceNode : sourceNodes) {
      if (Objects.equals(sourceNode.getId(), dataId)) {
        return sourceNode;
      }
    }
    return null;
  }

  @Override
  public DicomSourceNodeEntity updateSourceNode(ForwardNodeEntity forwardNodeEntity,
      DicomSourceNodeEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return null;
    }
    Collection<DicomSourceNodeEntity> sourceNodes = getAllSourceNodes(forwardNodeEntity);
    if (!sourceNodes.contains(data)) {
      forwardNodeEntity.addSourceNode(data);
    }
    return data;
  }

  @Override
  public void deleteSourceNode(ForwardNodeEntity forwardNodeEntity, DicomSourceNodeEntity data) {
    if (forwardNodeEntity == null || data == null) {
      return;
    }
    forwardNodeEntity.removeSourceNode(data);
  }

  @Override
  public List<SOPClassUIDEntity> getAllSOPClassUIDs() {
    List<SOPClassUIDEntity> list = new ArrayList<>();
    sopClassUIDRepo.findAll() //
        .forEach(list::add);
    return list;
  }

  @Override
  public SOPClassUIDEntity getSOPClassUIDByName(String name) {
    return sopClassUIDRepo.getSOPClassUIDByName(name);
  }

  @Override
  public SOPClassUIDEntity getSOPClassUIDById(Long dataId) {
    return sopClassUIDRepo.getSOPClassUIDById(dataId);
  }
}
