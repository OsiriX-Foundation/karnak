package org.karnak.data;

import java.util.Objects;

import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.SourceNode;
import org.springframework.context.ApplicationEvent;

public class OutputNodeEvent extends ApplicationEvent {
    private final NodeEventType eventType;
    private final ForwardNode forwardNode;

    public OutputNodeEvent(ForwardNode fwdNode, NodeEventType eventType) {
        super(fwdNode);
        this.forwardNode = fwdNode;
        this.eventType = eventType;
    }

    public OutputNodeEvent(SourceNode srcNode, NodeEventType eventType) {
        super(srcNode);
        this.forwardNode = Objects.requireNonNull(srcNode.getForwardNode());
        this.eventType = eventType;
    }
    
    public OutputNodeEvent(Destination dstNode, NodeEventType eventType) {
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