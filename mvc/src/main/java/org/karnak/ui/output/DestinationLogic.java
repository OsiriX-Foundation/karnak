package org.karnak.ui.output;

import java.io.Serializable;

import org.karnak.data.output.Destination;
import org.karnak.data.output.ForwardNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;

/**
 * This class provides an interface for the logical operations between the CRUD view, its parts like the data editor
 * form and the data source, including fetching and saving destinations.
 */
public class DestinationLogic implements Serializable {
    private static final long serialVersionUID = -1056308023882753627L;

    private final OutputLogic outputLogic;
    private final DestinationView view;

    public DestinationLogic(OutputLogic outputLogic, DestinationView view) {
        this.outputLogic = outputLogic;
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

    public OutputLogic getOutputLogic() {
        return outputLogic;
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
        outputLogic.validateView();
    }

    public void deleteDestination(Destination data) {
        view.clearSelection();
        view.removeDestination(data);
        view.showSaveNotification(data.getStringReference() + " removed");
        outputLogic.validateView();
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
