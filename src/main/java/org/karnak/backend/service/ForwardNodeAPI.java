/*
* TODO: REMOVE THIS USELESS PACKAGE/CLASS
* */
package org.karnak.backend.service;

import java.io.Serializable;
import java.util.Optional;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.enums.NodeEventType;
import org.karnak.backend.model.NodeEvent;
import org.springframework.context.ApplicationEventPublisher;

public class ForwardNodeAPI implements Serializable {
    private final ForwardNodeDataProvider dataProvider;
    private ApplicationEventPublisher applicationEventPublisher;

    public ForwardNodeAPI(ForwardNodeDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public ForwardNodeDataProvider getDataProvider() {
        return dataProvider;
    }

    public void addForwardNode(ForwardNodeEntity data) {
        NodeEventType eventType = data.isNewData() ? NodeEventType.ADD : NodeEventType.UPDATE;
        if (eventType == NodeEventType.ADD) {
            Optional<ForwardNodeEntity> val = dataProvider.getDataService().getAllForwardNodes()
                .stream()
                .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
            if (val.isPresent()) {
                // showError("Cannot add this new node because the AE-Title already exists!");
                return;
            }
        }
        dataProvider.save(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, eventType));
    }

    public void updateForwardNode(ForwardNodeEntity data) {
        dataProvider.save(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.UPDATE));
    }

    public void deleteForwardNode(ForwardNodeEntity data) {
        dataProvider.delete(data);
        applicationEventPublisher.publishEvent(new NodeEvent(data, NodeEventType.REMOVE));
    }

    public ForwardNodeEntity getForwardNodeById(Long dataId) {
        return dataProvider.get(dataId);
    }


    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
