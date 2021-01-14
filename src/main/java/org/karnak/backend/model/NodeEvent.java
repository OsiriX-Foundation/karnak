package org.karnak.backend.model;

import java.util.Objects;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.springframework.context.ApplicationEvent;

public class NodeEvent extends ApplicationEvent {
    private static final long serialVersionUID = -15504960651765311L;

  private final NodeEventType eventType;
  private final ForwardNodeEntity forwardNodeEntity;

  public NodeEvent(ForwardNodeEntity fwdNode, NodeEventType eventType) {
    super(fwdNode);
    this.forwardNodeEntity = fwdNode;
    this.eventType = eventType;
  }

  public NodeEvent(DicomSourceNodeEntity srcNode, NodeEventType eventType) {
    super(srcNode);
    this.forwardNodeEntity = Objects.requireNonNull(srcNode.getForwardNodeEntity());
    this.eventType = eventType;
  }

  public NodeEvent(DestinationEntity dstNode, NodeEventType eventType) {
    super(dstNode);
    this.forwardNodeEntity = Objects.requireNonNull(dstNode.getForwardNodeEntity());
    this.eventType = eventType;
  }

  public ForwardNodeEntity getForwardNode() {
    return forwardNodeEntity;
  }

  public NodeEventType getEventType() {
    return eventType;
  }
}