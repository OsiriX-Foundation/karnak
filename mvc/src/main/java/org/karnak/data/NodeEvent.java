package org.karnak.data;

import java.util.Objects;

import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.DicomSourceNode;
import org.karnak.data.gateway.ForwardNode;
import org.springframework.context.ApplicationEvent;

public class NodeEvent extends ApplicationEvent {
    private static final long serialVersionUID = -15504960651765311L;
    
    private final NodeEventType eventType;
    private final ForwardNode forwardNode;

    public NodeEvent(ForwardNode fwdNode, NodeEventType eventType) {
        super(fwdNode);
        this.forwardNode = fwdNode;
        this.eventType = eventType;
    }

    public NodeEvent(DicomSourceNode srcNode, NodeEventType eventType) {
        super(srcNode);
        this.forwardNode = Objects.requireNonNull(srcNode.getForwardNode());
        this.eventType = eventType;
    }
    
    public NodeEvent(Destination dstNode, NodeEventType eventType) {
        super(dstNode);
        this.forwardNode = Objects.requireNonNull(dstNode.getForwardNode());
        this.eventType = eventType;
    }

    public ForwardNode getForwardNode() {
        return forwardNode;
    }

    public NodeEventType getEventType() {
        return eventType;
    }
}