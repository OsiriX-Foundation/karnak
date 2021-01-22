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
public class ForwardNodeAPIService implements Serializable {

    private final ForwardNodeService forwardNodeService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ForwardNodeAPIService(final ForwardNodeService forwardNodeService) {
        this.forwardNodeService = forwardNodeService;
    }

    public ForwardNodeService getDataProvider() {
        return forwardNodeService;
    }

    public void addForwardNode(ForwardNodeEntity forwardNodeEntity) {
        NodeEventType eventType =
            forwardNodeEntity.getId() == null ? NodeEventType.ADD : NodeEventType.UPDATE;
        if (eventType == NodeEventType.ADD) {
            Optional<ForwardNodeEntity> val = forwardNodeService.getAllForwardNodes()
                .stream()
                .filter(f -> f.getFwdAeTitle().equals(forwardNodeEntity.getFwdAeTitle()))
                .findFirst();
            if (val.isPresent()) {
                // showError("Cannot add this new node because the AE-Title already exists!");
                return;
            }
        }
        forwardNodeService.save(forwardNodeEntity);
        applicationEventPublisher.publishEvent(new NodeEvent(forwardNodeEntity, eventType));
    }

    public void updateForwardNode(ForwardNodeEntity forwardNodeEntity) {
        forwardNodeService.save(forwardNodeEntity);
        applicationEventPublisher
            .publishEvent(new NodeEvent(forwardNodeEntity, NodeEventType.UPDATE));
    }

    public void deleteForwardNode(ForwardNodeEntity forwardNodeEntity) {
        forwardNodeService.delete(forwardNodeEntity);
        applicationEventPublisher
            .publishEvent(new NodeEvent(forwardNodeEntity, NodeEventType.REMOVE));
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
