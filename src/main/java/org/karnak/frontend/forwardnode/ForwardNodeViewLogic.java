package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.UI;
import org.karnak.backend.data.entity.ForwardNode;
import org.karnak.backend.service.ForwardNodeAPI;

public class ForwardNodeViewLogic {
    private final ForwardNodeAPI forwardNodeAPI;

    public ForwardNodeViewLogic(ForwardNodeAPI forwardNodeAPI) {
        this.forwardNodeAPI = forwardNodeAPI;
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
        UI.getCurrent().navigate(ForwardNodeView.class, fragmentParameter);
    }

    public Long enter(String dataIdStr) {
        // TODO: On enter, go to dataIdStr
        try {
            Long dataId = Long.valueOf(dataIdStr);
            return dataId;
        } catch (NumberFormatException e) {
        }
        return null;
        /*
        if (dataIdStr != null && !dataIdStr.isEmpty()) {
            // Ensure this is selected even if coming directly here from login
            try {
                Long dataId = Long.valueOf(dataIdStr);
                ForwardNode data = findForwardNode(dataId);
                gatewayView.selectRow(data);
            } catch (NumberFormatException e) {
            }
        } else {
            gatewayView.showForm(false);
        }
        */
    }

    public void editForwardNode(ForwardNode data) {
        if (data == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(String.valueOf(data.getId()));
        }
    }

    public void cancelForwardNode() {
        setFragmentParameter("");
    }


    public void saveForwardNode(ForwardNode data) {
        /*
        boolean newData = data.isNewData();
        gatewayView.clearSelection();
        gatewayView.updateForwardNode(data);
        setFragmentParameter("");
        gatewayView.showSaveNotification(data.getFwdAeTitle() + (newData ? " created" : " updated"));
        //editForwardNode(data); //if you dont't want to exit the selection after saving a forward node.
        editForwardNode(null); //if you want to exit the selection after saving a forward node.
         */
    }

    public void deleteForwardNode(ForwardNode data) {
        /*
        gatewayView.clearSelection();
        gatewayView.removeForwardNode(data);
        setFragmentParameter("");
        gatewayView.showSaveNotification(data.getFwdAeTitle() + " removed");
        */
        setFragmentParameter("");
    }
}
