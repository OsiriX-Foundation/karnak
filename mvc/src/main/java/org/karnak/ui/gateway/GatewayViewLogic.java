package org.karnak.ui.gateway;

import java.io.Serializable;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.ui.authentication.AccessControl;
import org.karnak.ui.authentication.AccessControlFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.flow.component.UI;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the data editor form and the data source, including
 * fetching and saving forward nodes.
 */
public class GatewayViewLogic implements Serializable {
    private static final long serialVersionUID = -8780527437240110630L;

    private final GatewayView gatewayView;
    private ApplicationEventPublisher applicationEventPublisher;

    public GatewayViewLogic(GatewayView gatewayView) {
        this.gatewayView = gatewayView;
    }

    public void init() {
        editForwardNode(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            gatewayView.setNewForwardNodeEnabled(false);
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

        UI.getCurrent().navigate(GatewayView.class, fragmentParameter);
    }

    public void enter(String dataIdStr) {
        if (dataIdStr != null && !dataIdStr.isEmpty()) {
            if (dataIdStr.equals("new")) {
                newForwardNode();
            } else {
                // Ensure this is selected even if coming directly here from login
                try {
                    Long dataId = Long.valueOf(dataIdStr);
                    ForwardNode data = findForwardNode(dataId);
                    gatewayView.selectRow(data);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            gatewayView.showForm(false);
        }
    }

    private ForwardNode findForwardNode(Long dataId) {
        return gatewayView.getForwardNodeById(dataId);
    }

    public void cancelForwardNode() {
        gatewayView.clearSelection();
        editForwardNode(null);
        setFragmentParameter("");
    }

    public void saveForwardNode(ForwardNode data) {
        boolean newData = data.isNewData();
        gatewayView.clearSelection();
        gatewayView.updateForwardNode(data);
        setFragmentParameter("");
        gatewayView.showSaveNotification(data.getFwdAeTitle() + (newData ? " created" : " updated"));
        //editForwardNode(data); //if you dont't want to exit the selection after saving a forward node.
        editForwardNode(null); //if you want to exit the selection after saving a forward node.
    }

    public void deleteForwardNode(ForwardNode data) {
        gatewayView.clearSelection();
        gatewayView.removeForwardNode(data);
        setFragmentParameter("");
        gatewayView.showSaveNotification(data.getFwdAeTitle() + " removed");
    }

    public void discardForwardNode(ForwardNode data) {
        if (data == null || data.isNewData()) {
            newForwardNode();
        } else {
            editForwardNode(findForwardNode(data.getId()));
        }
    }

    public void editForwardNode(ForwardNode data) {
        if (data == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(String.valueOf(data.getId()));
        }
        gatewayView.editForwardNode(data);
    }

    public void newForwardNode() {
        gatewayView.clearSelection();
        setFragmentParameter("new");
        gatewayView.editForwardNode(ForwardNode.ofEmpty());
    }

    public void rowSelected(ForwardNode data) {
        if (AccessControlFactory.getInstance().createAccessControl().isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editForwardNode(data);
        }
    }

    public void validateView() {
        gatewayView.validateView();
    }

    public void showForwardNodeForm(boolean show){
        if(gatewayView.getForwardNodeForm() != null){
            gatewayView.getForwardNodeForm().showForm(show);
        }
    }

    public void saveForwardNode(){
        gatewayView.getForwardNodeForm().saveForwardNode();
    }
}
