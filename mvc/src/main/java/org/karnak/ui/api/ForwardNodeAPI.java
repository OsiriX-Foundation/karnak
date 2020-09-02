package org.karnak.ui.api;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.gateway.ForwardNodeDataProvider;

import java.util.Optional;

public class ForwardNodeAPI {
    private final ForwardNodeDataProvider dataProvider;

    public ForwardNodeAPI(ForwardNodeDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public ForwardNodeDataProvider getDataProvider() {
        return dataProvider;
    }

    public void addForwardNode(ForwardNode data) {
        Optional<ForwardNode> val = dataProvider.getDataService().getAllForwardNodes().stream()
                .filter(f -> f.getFwdAeTitle().equals(data.getFwdAeTitle())).findFirst();
        if (val.isPresent()) {
            // showError("Cannot add this new node because the AE-Title already exists!");
            return;
        }
        dataProvider.save(data);
    }

    public ForwardNode getForwardNodeById(Long dataId) {
        return dataProvider.get(dataId);
    }
}
