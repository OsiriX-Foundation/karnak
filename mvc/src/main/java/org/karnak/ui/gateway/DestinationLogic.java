package org.karnak.ui.gateway;

import java.io.Serializable;

import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;

/**
 * This class provides an interface for the logical operations between the CRUD view, its parts like the data editor
 * form and the data source, including fetching and saving destinations.
 */
public class DestinationLogic implements Serializable {
    private static final long serialVersionUID = -1056308023882753627L;

    private final GatewayViewLogic gatewayViewLogic;
    private final DestinationView view;

    public DestinationLogic(GatewayViewLogic gatewayViewLogic, DestinationView view) {
        this.gatewayViewLogic = gatewayViewLogic;
        this.view = view;
    }

    public void init(ForwardNode forwardNode) {
        view.setForwardNode(forwardNode);
        editDestination(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewDestinationEnabled(false);
        }
    }

    public GatewayViewLogic getGatewayViewLogic() {
        return gatewayViewLogic;
    }

    public void cancelDestination() {
        view.clearSelection();
        view.cancelDestination();
    }

    public void saveDestination(Destination data) {
        boolean newData = data.isNewData();
        view.clearSelection();
        view.updateDestination(data);
        view.showSaveNotification(data.getStringReference() + (newData ? " created" : " updated"));
        gatewayViewLogic.validateView();
    }

    public void deleteDestination(Destination data) {
        view.clearSelection();
        view.removeDestination(data);
        view.showSaveNotification(data.getStringReference() + " removed");
        gatewayViewLogic.validateView();
    }

    public void editDestination(Destination data) {
        view.editDestination(data);
    }

    public void newDestinationDicom() {
        view.clearSelection();
        view.editDestination(Destination.ofDicomEmpty());
    }

    public void newDestinationStow() {
        view.clearSelection();
        view.editDestination(Destination.ofStowEmpty());
    }

    public void rowSelected(Destination data) {
        if (AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editDestination(data);
        }
    }
}
