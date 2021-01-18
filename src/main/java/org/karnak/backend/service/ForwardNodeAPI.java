/*
* TODO: REMOVE THIS USELESS PACKAGE/CLASS
* */
package org.karnak.backend.service;

import java.io.Serializable;
import java.util.Optional;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ForwardNodeAPI implements Serializable {

    private final ForwardNodeService forwardNodeService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ForwardNodeAPI(final ForwardNodeService forwardNodeService) {
        this.forwardNodeService = forwardNodeService;
    }

    public ForwardNodeService getDataProvider() {
        return forwardNodeService;
    }

    public void addForwardNode(ForwardNodeEntity data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
        if (eventType == NodeEventType.ADD) {
            Optional<ForwardNodeEntity> val = forwardNodeService.getAllForwardNodes()
                .stream()
                .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
            if (val.isPresent()) {
                // showError("Cannot add this new node because the AE-Title already exists!");
                return;
            }
        }
        forwardNodeService.save(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, eventType));
    }

    public void updateForwardNode(ForwardNodeEntity data) {
        forwardNodeService.save(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.UPDATE));
    }

    public void deleteForwardNode(ForwardNodeEntity data) {
        forwardNodeService.delete(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.REMOVE));
    }

    public ForwardNodeEntity getForwardNodeById(Long dataId) {
        return forwardNodeService.get(dataId);
    }


    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
