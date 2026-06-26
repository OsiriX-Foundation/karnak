/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.dicom.monitor;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.jspecify.annotations.NullUnmarked;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.backend.util.DicomNodeUtil;
import org.karnak.frontend.dicom.AbstractView;
import org.weasis.core.util.annotations.Generated;

@Generated()
@NullUnmarked
public class MonitorView extends AbstractView {

	// CONTROLLER
	private final MonitorLogic logic = new MonitorLogic(this);

	// UI COMPONENTS
	private VerticalLayout dicomLayout;

	// Dicom Layout
	private HorizontalLayout dicomEchoLayout;

	private H6 dicomEchoLayoutTitle;

	private Select<DicomNodeList> dicomEchoNodeListSelector;

	private Button dicomEchoBtn;

	// Result Layout
	private VerticalLayout resultLayout;

	private H6 resultTitle;

	private Div resultDiv;

	private final DicomNodeUtil dicomNodeUtil;

	public MonitorView(DicomNodeUtil dicomNodeUtil) {
		this.dicomNodeUtil = dicomNodeUtil;
		init();
		createView();
		createMainLayout();

		add(mainLayout);
	}

	public void displayStatus(String status) {
		resultDiv.removeAll();
		resultDiv.add(new Html("<span>" + status + "</span>"));

		resultLayout.setVisible(true);
	}

	private void init() {
	}

	private void createView() {
		setSizeFull();
	}

	private void createMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setPadding(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidthFull();

		buildDicomLayout();
		buildResultLayout();

		mainLayout.add(dicomLayout, resultLayout);
	}

	private void buildDicomEchoLayoutTitle() {
		dicomEchoLayoutTitle = new H6("Dicom Echo");
		dicomEchoLayoutTitle.getStyle().set("margin-top", "0px");
	}

	private void buildDicomEchoLayout() {
		dicomEchoLayout = new HorizontalLayout();
		dicomEchoLayout.setMargin(false);
		dicomEchoLayout.setSpacing(true);
		dicomEchoLayout.setWidthFull();
		dicomEchoLayout.setDefaultVerticalComponentAlignment(Alignment.END);

		buildDicomNodeListSelector();
		buildDicomEchoBtn();

		dicomEchoLayout.add(dicomEchoNodeListSelector, dicomEchoBtn);
	}

	private void buildDicomNodeListSelector() {
		dicomEchoNodeListSelector = new Select<>();
		dicomEchoNodeListSelector.setEmptySelectionAllowed(false);

		var dicomNodeTypes = dicomNodeUtil.getAllDicomNodeTypes();

		dicomEchoNodeListSelector.setItems(dicomNodeTypes);

		dicomEchoNodeListSelector
			.addValueChangeListener((ValueChangeListener<ValueChangeEvent<DicomNodeList>>) event -> logic
				.dicomNodeListSelected(event.getValue()));

		if (!dicomNodeTypes.isEmpty()) {
			dicomEchoNodeListSelector.setValue(dicomNodeTypes.getFirst());
		}
	}

	private void buildDicomEchoBtn() {
		dicomEchoBtn = new Button("Check!");
		dicomEchoBtn.addClickListener(event -> logic.dicomEcho());
	}

	private void buildDicomLayout() {
		dicomLayout = new VerticalLayout();
		dicomLayout.setWidthFull();
		dicomLayout.setPadding(true);
		dicomLayout.setSpacing(false);
		dicomLayout.getStyle()
			.set("box-shadow",
					"0 2px 1px -1px rgba(0,0,0,.2), 0 1px 1px 0 rgba(0,0,0,.14), 0 1px 3px 0 rgba(0,0,0,.12)");
		dicomLayout.getStyle().set("border-radius", "4px");

		buildDicomEchoLayoutTitle();
		buildDicomEchoLayout();

		dicomLayout.add(dicomEchoLayoutTitle, dicomEchoLayout);
	}

	private void buildResultLayout() {
		resultLayout = new VerticalLayout();
		resultLayout.setSizeFull();
		resultLayout.setPadding(true);
		resultLayout.setSpacing(false);
		resultLayout.getStyle()
			.set("box-shadow",
					"0 2px 1px -1px rgba(0,0,0,.2), 0 1px 1px 0 rgba(0,0,0,.14), 0 1px 3px 0 rgba(0,0,0,.12)");
		resultLayout.getStyle().set("border-radius", "4px");
		resultLayout.setVisible(false);

		buildResultTitle();
		buildResultDiv();

		resultLayout.add(resultTitle, resultDiv);
	}

	private void buildResultTitle() {
		resultTitle = new H6("Result");
		resultTitle.getStyle().set("margin-top", "0px");
		resultTitle.getStyle().set("padding-bottom", "0px");
	}

	private void buildResultDiv() {
		resultDiv = new Div();
		resultDiv.setSizeFull();
		resultDiv.getStyle().set("overflow-y", "auto");
	}

}
