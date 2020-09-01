package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.UI;
import org.karnak.data.gateway.ForwardNode;

public class ForwardNodeViewLogic {
    private final ForwardNodeView forwardNodeView;

    public ForwardNodeViewLogic(ForwardNodeView forwardNodeView) {
        this.forwardNodeView = forwardNodeView;
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

    public void enter(String dataIdStr) {
        // TODO: On enter, go to dataIdStr
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
}
