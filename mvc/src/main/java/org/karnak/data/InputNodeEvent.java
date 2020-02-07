package org.karnak.data;

import org.springframework.context.ApplicationEvent;

public class InputNodeEvent extends ApplicationEvent {
    private final NodeEventType eventType;

    public InputNodeEvent(Object source, NodeEventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public NodeEventType getEventType() {
        return eventType;
    }
}