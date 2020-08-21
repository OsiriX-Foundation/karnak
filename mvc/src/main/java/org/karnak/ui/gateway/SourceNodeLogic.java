package org.karnak.ui.gateway;

import java.io.Serializable;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.DicomSourceNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the data editor form and the data source, including
 * fetching and saving source nodes.
 */
public class SourceNodeLogic implements Serializable {
    private static final long serialVersionUID = -1056308023882753627L;

    private final GatewayViewLogic gatewayViewLogic;
    private final SourceNodeView view;

    public SourceNodeLogic(GatewayViewLogic gatewayViewLogic, SourceNodeView view) {
        this.gatewayViewLogic = gatewayViewLogic;
        this.view = view;
    }

    public void init(ForwardNode forwardNode) {
        view.setForwardNode(forwardNode);
        editSourceNode(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewSourceNodeEnabled(false);
        }
    }

    public void cancelSourceNode() {
        view.clearSelection();
        view.cancelDestination();
    }

    public GatewayViewLogic getGatewayViewLogic() {
        return gatewayViewLogic;
    }

    public void saveSourceNode(DicomSourceNode data) {
        boolean newData = data.isNewData();
        view.clearSelection();
        view.updateSourceNode(data);
        view.showSaveNotification(data.getAeTitle() + (newData ? " created" : " updated"));
        gatewayViewLogic.validateView();
    }

    public void deleteSourceNode(DicomSourceNode data) {
        view.clearSelection();
        view.removeSourceNode(data);
        view.showSaveNotification(data.getAeTitle() + " removed");
        gatewayViewLogic.validateView();
    }

    public void editSourceNode(DicomSourceNode data) {
        view.editSourceNode(data);
    }

    public void newSourceNode() {
        view.clearSelection();
        view.editSourceNode(DicomSourceNode.ofEmpty());
    }

    public void rowSelected(DicomSourceNode data) {
        if (AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editSourceNode(data);
        }
    }
}
