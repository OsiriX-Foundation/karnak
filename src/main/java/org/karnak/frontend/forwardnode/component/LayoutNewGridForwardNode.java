/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.data.entity.ForwardNodeEntity;

public class LayoutNewGridForwardNode extends VerticalLayout {

	// UI Components
	private final NewForwardNode newForwardNode;

	private final GridForwardNode gridForwardNode;

	private final Button buttonNewForwardNode;

	private final TextField textFieldNewAETitleForwardNode;

	private final Button buttonAddNewForwardNode;

	private Button buttonCancelNewForwardNode;

	public LayoutNewGridForwardNode() {
		newForwardNode = new NewForwardNode();
		gridForwardNode = new GridForwardNode();

		add(newForwardNode, gridForwardNode);

		buttonNewForwardNode = newForwardNode.getNewForwardNodeBtn();
		textFieldNewAETitleForwardNode = newForwardNode.getNewAETitleForwardNode();
		buttonAddNewForwardNode = newForwardNode.getAddNewForwardNode();
	}

	public void load(ForwardNodeEntity forwardNodeEntity) {
		if (forwardNodeEntity != null && forwardNodeEntity != gridForwardNode.getSelectedRow()) {
			gridForwardNode.selectRow(forwardNodeEntity);
		}
		else {
			gridForwardNode.getSelectionModel().deselectAll();
		}
	}

	public GridForwardNode getGridForwardNode() {
		return gridForwardNode;
	}

	public TextField getTextFieldNewAETitleForwardNode() {
		return textFieldNewAETitleForwardNode;
	}

	public Button getButtonAddNewForwardNode() {
		return buttonAddNewForwardNode;
	}

}
