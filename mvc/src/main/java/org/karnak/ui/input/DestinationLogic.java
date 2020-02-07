package org.karnak.ui.input;

import java.io.Serializable;

import org.karnak.data.input.Destination;
import org.karnak.data.input.SourceNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;

/**
 * This class provides an interface for the logical operations between the CRUD view, its parts like the data editor
 * form and the data source, including fetching and saving destinations.
 */
public class DestinationLogic implements Serializable {
    private static final long serialVersionUID = -1056308023882753627L;

    private final InputLogic inputLogic;
    private final DestinationView view;

    public DestinationLogic(InputLogic inputLogic, DestinationView view) {
        this.inputLogic = inputLogic;
        this.view = view;
    }

    public void init(SourceNode sourceNode) {
        view.setSourceNode(sourceNode);
        editDestination(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewDestinationEnabled(false);
        }
    }

    public InputLogic getInputLogic() {
        return inputLogic;
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
        inputLogic.validateView();
    }

    public void deleteDestination(Destination data) {
        view.clearSelection();
        view.removeDestination(data);
        view.showSaveNotification(data.getStringReference() + " removed");
        inputLogic.validateView();
    }

    public void editDestination(Destination data) {
        view.editDestination(data);
    }

    public void newDestination() {
        view.clearSelection();
        view.editDestination(Destination.ofEmpty());
    }

    public void rowSelected(Destination data) {
        if (AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editDestination(data);
        }
    }
}
