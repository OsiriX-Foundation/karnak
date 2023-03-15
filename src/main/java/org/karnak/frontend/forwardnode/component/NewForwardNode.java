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

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class NewForwardNode extends HorizontalLayout {

	private final Button newForwardNode;

	private final TextField newAETitleForwardNode;

	private final Button addNewForwardNode;

	private final Button cancelNewForwardNode;

	public NewForwardNode() {
		newAETitleForwardNode = new TextField();
		addNewForwardNode = new Button("Add");
		cancelNewForwardNode = new Button("Cancel");
		newForwardNode = new Button("New forward node");
		initView();
	}

	private void initView() {
		setNewAETitleForwardNode();
		setAddNewForwardNode();
		setCancelNewForwardNode();
		setNewForwardNode();
		add(newForwardNode);
	}

	private void setNewAETitleForwardNode() {
		newAETitleForwardNode.setPlaceholder("Forward AETitle");
		newAETitleForwardNode.addKeyDownListener(Key.ENTER, keyDownEvent -> {
			removeAll();
			add(newForwardNode);
		});
	}

	private void setAddNewForwardNode() {
		addNewForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		addNewForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
		addNewForwardNode.addClickListener(click -> {
			removeAll();
			add(newForwardNode);
		});
	}

	private void setCancelNewForwardNode() {
		cancelNewForwardNode.addClickListener(click -> {
			removeAll();
			add(newForwardNode);
		});
	}

	private void setNewForwardNode() {
		newForwardNode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		newForwardNode.setIcon(VaadinIcon.PLUS_CIRCLE.create());
		newForwardNode.addClickListener(click -> {
			removeAll();
			newAETitleForwardNode.setValue("");
			add(newAETitleForwardNode, addNewForwardNode, cancelNewForwardNode);
		});
		// CTRL+N will create a new window which is unavoidable
		newForwardNode.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
	}

	public Button getNewForwardNode() {
		return newForwardNode;
	}

	public TextField getNewAETitleForwardNode() {
		return newAETitleForwardNode;
	}

	public Button getAddNewForwardNode() {
		return addNewForwardNode;
	}

	public Button getCancelNewForwardNode() {
		return cancelNewForwardNode;
	}

}
