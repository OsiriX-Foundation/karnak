/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.UI;
import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.karnak.backend.service.ForwardNodeAPIService;
import org.springframework.stereotype.Service;

@Service
public class ForwardNodeViewLogic {

  private final ForwardNodeAPIService forwardNodeAPIService;

  public ForwardNodeViewLogic(ForwardNodeAPIService forwardNodeAPIService) {
    this.forwardNodeAPIService = forwardNodeAPIService;
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
            ForwardNodeEntity data = findForwardNode(dataId);
            gatewayView.selectRow(data);
        } catch (NumberFormatException e) {
        }
    } else {
        gatewayView.showForm(false);
    }
    */
  }

  public void editForwardNode(ForwardNodeEntity data) {
    if (data == null) {
      setFragmentParameter("");
    } else {
      setFragmentParameter(String.valueOf(data.getId()));
    }
  }

  public void cancelForwardNode() {
    setFragmentParameter("");
  }

  public void saveForwardNode(ForwardNodeEntity data) {
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

  public void deleteForwardNode(ForwardNodeEntity data) {
    /*
    gatewayView.clearSelection();
    gatewayView.removeForwardNode(data);
    setFragmentParameter("");
    gatewayView.showSaveNotification(data.getFwdAeTitle() + " removed");
    */
    setFragmentParameter("");
  }
}
