package org.karnak.ui.input;

import java.io.Serializable;

import org.karnak.data.input.SourceNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.flow.component.UI;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the data editor form and the data source, including
 * fetching and saving source nodes.
 */
public class InputLogic implements Serializable {
    private static final long serialVersionUID = -8780527437240110630L;

    private final InputView view;
    private ApplicationEventPublisher applicationEventPublisher;

    public InputLogic(InputView view) {
        this.view = view;
    }

    public void init() {
        editSourceNode(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewSourceNodeEnabled(false);
        }
    }
    
    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String dataIdStr) {
        final String fragmentParameter;
        if (dataIdStr == null || dataIdStr.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = dataIdStr;
        }

        UI.getCurrent().navigate(InputView.class, fragmentParameter);
    }

    public void enter(String dataIdStr) {
        if (dataIdStr != null && !dataIdStr.isEmpty()) {
            if (dataIdStr.equals("new")) {
                newSourceNode();
            } else {
                // Ensure this is selected even if coming directly here from login
                try {
                    Long dataId = Long.valueOf(dataIdStr);
                    SourceNode data = findSourceNode(dataId);
                    view.selectRow(data);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private SourceNode findSourceNode(Long dataId) {
        return view.getSourceNodeById(dataId);
    }

    public void cancelSourceNode() {
        view.clearSelection();
        editSourceNode(null);
        setFragmentParameter("");
    }

    public void saveSourceNode(SourceNode data) {
        boolean newData = data.isNewData();
        view.clearSelection();
        view.updateSourceNode(data);
        setFragmentParameter("");
        view.showSaveNotification(data.getStringReference() + (newData ? " created" : " updated"));
        editSourceNode(null);
    }

    public void deleteSourceNode(SourceNode data) {
        view.clearSelection();
        view.removeSourceNode(data);
        setFragmentParameter("");
        view.showSaveNotification(data.getStringReference() + " removed");
    }

    public void discardSourceNode(SourceNode data) {
        if (data == null || data.isNewData()) {
            newSourceNode();
        } else {
            editSourceNode(findSourceNode(data.getId()));
        }
    }

    public void editSourceNode(SourceNode data) {
        if (data == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(String.valueOf(data.getId()));
        }
        view.editSourceNode(data);
    }

    public void newSourceNode() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editSourceNode(SourceNode.ofEmpty());
    }

    public void rowSelected(SourceNode data) {
        if (AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editSourceNode(data);
        }
    }

    public void validateView() {
        view.validateView();
    }
}
