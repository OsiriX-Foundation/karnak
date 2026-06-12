/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.dicom.mwl;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Comparator;
import lombok.Getter;
import org.karnak.backend.enums.MessageFormat;
import org.karnak.backend.enums.MessageLevel;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.model.dicom.DicomNodeList;
import org.karnak.backend.model.dicom.Message;
import org.karnak.backend.util.DicomNodeUtil;
import org.karnak.frontend.component.AbstractDialog;
import org.karnak.frontend.dicom.PortField;
import org.weasis.core.util.annotations.Generated;

@Generated()
public class DicomWorkListSelectionDialog extends AbstractDialog {

	// CONTROLLER
	private final DicomWorkListSelectionLogic logic;

	private final DicomNodeUtil dicomNodeUtil;

	// UI COMPONENTS
	private Dialog dialog;

	private Div titleBar;

	private FormLayout formLayout;

	private Select<ConfigNode> worklistNodeSelector;

	private TextField editNameFld;

	private TextField editAetFld;

	private TextField editHostnameFld;

	private PortField editPortFld;

	private HorizontalLayout buttonBar;

	private Button cancelBtn;

	private Button deleteBtn;

	private Button updateBtn;

	private Button selectBtn;

	private HorizontalLayout ioBar;

	private Anchor exportAnchor;

	private Upload importUpload;

	private transient UI ui;

	// DATA
	private DicomNodeList workListNodes;

	private ListDataProvider<ConfigNode> dataProviderForWorkListNodes;

	public DicomWorkListSelectionDialog(DicomNodeUtil dicomNodeUtil) {
		this.dicomNodeUtil = dicomNodeUtil;
		this.ui = UI.getCurrent();
		this.logic = new DicomWorkListSelectionLogic(this, dicomNodeUtil);
		init();
		logic.loadDicomNodeList();
		createMainLayout();
		dataProviderForWorkListNodes.refreshAll();
		selectFirstItemInWorkListNodes();
		dialog.add(mainLayout);
	}

	@Override
	protected void createMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setPadding(false);
		mainLayout.setSpacing(true);

		buildTitleBar();
		buildFormLayout();
		buildIoBar();
		buildButtonBar();

		mainLayout.add(titleBar, formLayout, ioBar, buttonBar);
	}

	private void buildIoBar() {
		ioBar = new HorizontalLayout();
		ioBar.setWidthFull();
		ioBar.setPadding(false);
		ioBar.setSpacing(true);

		exportAnchor = new Anchor();
		exportAnchor.setHref(DownloadHandler.fromInputStream(event -> new DownloadResponse(
				new ByteArrayInputStream(dicomNodeUtil.exportWorkListNodes()), "worklists.csv", "text/csv", -1)));
		exportAnchor.getElement().setAttribute("download", true);
		exportAnchor.add(new Button("Export all worklists (CSV)"));

		importUpload = new Upload((UploadHandler) event -> {
			byte[] bytes;
			try (InputStream in = event.getInputStream()) {
				bytes = in.readAllBytes();
			}
			importNodesFromCsv(bytes);
		});
		importUpload.setDropLabel(new Span("Import worklists (CSV)"));
		importUpload.setMaxFiles(1);

		ioBar.add(exportAnchor, importUpload);
	}

	private void importNodesFromCsv(byte[] bytes) {
		try {
			int count = dicomNodeUtil.importWorkListNodes(new ByteArrayInputStream(bytes), ',');
			runOnUi(() -> {
				reloadWorkListNodes();
				displayMessage(
						new Message(MessageLevel.INFO, MessageFormat.TEXT, count + " worklist(s) imported from CSV"));
			});
		}
		catch (Exception e) {
			runOnUi(() -> displayMessage(new Message(MessageLevel.ERROR, MessageFormat.TEXT,
					"Cannot import the CSV file: " + e.getMessage())));
		}
	}

	private void runOnUi(Runnable command) {
		if (ui != null) {
			ui.access(command::run);
		}
		else {
			command.run();
		}
	}

	public void open() {
		dialog.open();
	}

	public void loadWorkListNodes(DicomNodeList workListNodes) {
		this.workListNodes.clear();

		if (workListNodes != null && !workListNodes.isEmpty()) {
			this.workListNodes.addAll(workListNodes);
			this.workListNodes.sort(Comparator.comparing(ConfigNode::getName));
		}
	}

	// LISTENERS
	public void addWorkListSelectionListener(ComponentEventListener<WorkListSelectionEvent> listener) {
		addListener(WorkListSelectionEvent.class, listener);
	}

	private void init() {
		dialog = this.getContent();
		dialog.setWidth("500px");

		workListNodes = new DicomNodeList("Worklists");
		buildDataProvider();
	}

	private void buildDataProvider() {
		dataProviderForWorkListNodes = new ListDataProvider<>(workListNodes);

		dataProviderForWorkListNodes.addDataProviderListener(e -> selectFirstItemInWorkListNodes());
	}

	private void buildTitleBar() {
		titleBar = new Div();
		titleBar.setText("Worklist Selection");
		titleBar.getStyle().set("font-weight", "500");
	}

	private void buildFormLayout() {
		formLayout = new FormLayout();
		formLayout.setSizeFull();

		buildWorklistNodeSelector();
		buildEditFields();

		formLayout.add(worklistNodeSelector, editNameFld, editAetFld, editHostnameFld, editPortFld);
	}

	private void buildEditFields() {
		editNameFld = new TextField("Description");
		editAetFld = new TextField("AET");
		editHostnameFld = new TextField("Hostname");
		editPortFld = new PortField();
		editPortFld.setLabel("Port");
	}

	private void populateEditFields(ConfigNode node) {
		if (node == null) {
			editNameFld.clear();
			editAetFld.clear();
			editHostnameFld.clear();
			editPortFld.clear();
			deleteBtn.setEnabled(false);
			updateBtn.setEnabled(false);
			return;
		}

		editNameFld.setValue(node.getName());
		editAetFld.setValue(node.getAet());
		editHostnameFld.setValue(node.getHostname());
		editPortFld.setValue(node.getPort());

		boolean persisted = node.getId() != null;
		deleteBtn.setEnabled(persisted);
		updateBtn.setEnabled(persisted);
	}

	private void reloadWorkListNodes() {
		logic.loadDicomNodeList();
		dataProviderForWorkListNodes.refreshAll();
		selectFirstItemInWorkListNodes();
	}

	private void buildWorklistNodeSelector() {
		worklistNodeSelector = new Select<>();
		worklistNodeSelector.setLabel("Worklist Node");
		worklistNodeSelector.setDataProvider(dataProviderForWorkListNodes);
		worklistNodeSelector.setItemLabelGenerator(item -> item.getName() + " [" + item.getAet() + " | "
				+ item.getHostname() + " | " + item.getPort() + "]");
		worklistNodeSelector.setRenderer(buildDicomNodeRenderer());

		worklistNodeSelector.addValueChangeListener(event -> populateEditFields(event.getValue()));
	}

	private ComponentRenderer<Div, ConfigNode> buildDicomNodeRenderer() {
		return getDivConfigNodeComponentRenderer();
	}

	public static ComponentRenderer<Div, ConfigNode> getDivConfigNodeComponentRenderer() {
		return new ComponentRenderer<>(item -> {
			Div div = new Div();
			div.getStyle().set("line-height", "92%");

			Span spanDescription = new Span(item.getName());
			spanDescription.getStyle().set("font-weight", "500");

			HtmlComponent htmlLineBreak = new HtmlComponent("BR");

			Span spanOtherAttributes = new Span(item.getAet() + " | " + item.getHostname() + " | " + item.getPort());
			spanOtherAttributes.getStyle().set("font-size", "75%");

			div.add(spanDescription, htmlLineBreak, spanOtherAttributes);

			return div;
		});
	}

	private void selectFirstItemInWorkListNodes() {
		if (workListNodes != null && !workListNodes.isEmpty()) {
			worklistNodeSelector.setValue(workListNodes.getFirst());
		}
	}

	private void buildButtonBar() {
		buttonBar = new HorizontalLayout();
		buttonBar.setWidthFull();
		buttonBar.setPadding(false);
		buttonBar.setSpacing(true);

		buildCancelBtn();
		buildDeleteBtn();
		buildUpdateBtn();
		buildSelectBtn();

		buttonBar.add(cancelBtn, deleteBtn, updateBtn, selectBtn);
	}

	private void buildDeleteBtn() {
		deleteBtn = new Button("Delete");
		deleteBtn.setEnabled(false);

		deleteBtn.addClickListener(e -> deleteSelectedNode());
	}

	private void buildUpdateBtn() {
		updateBtn = new Button("Save changes");
		updateBtn.setEnabled(false);

		updateBtn.addClickListener(e -> updateSelectedNode());
	}

	private void deleteSelectedNode() {
		ConfigNode selected = worklistNodeSelector.getValue();
		if (selected == null || selected.getId() == null) {
			return;
		}

		try {
			dicomNodeUtil.deleteDicomNode(selected.getId());
			reloadWorkListNodes();
		}
		catch (Exception ex) {
			displayMessage(new Message(MessageLevel.ERROR, MessageFormat.TEXT,
					"Cannot delete the worklist: " + ex.getMessage()));
		}
	}

	private void updateSelectedNode() {
		ConfigNode selected = worklistNodeSelector.getValue();
		if (selected == null || selected.getId() == null) {
			return;
		}

		if (editAetFld.isEmpty() || editHostnameFld.isEmpty() || editPortFld.isEmpty()) {
			displayMessage(new Message(MessageLevel.WARN, MessageFormat.TEXT, "AET, Hostname and Port are required"));
			return;
		}

		try {
			dicomNodeUtil.updateDicomNode(selected.getId(), editNameFld.getValue(), editAetFld.getValue(),
					editHostnameFld.getValue(), editPortFld.getValue());
			reloadWorkListNodes();
		}
		catch (Exception ex) {
			displayMessage(new Message(MessageLevel.ERROR, MessageFormat.TEXT,
					"Cannot update the worklist: " + ex.getMessage()));
		}
	}

	private void buildCancelBtn() {
		cancelBtn = new Button("Cancel");

		cancelBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				dialog.close();
			}
		});
	}

	private void buildSelectBtn() {
		selectBtn = new Button("Select");
		selectBtn.addClassName("stroked-button");

		selectBtn.addClickListener(e -> {
			fireWorkListSelectionEvent();
			dialog.close();
		});
	}

	private void fireWorkListSelectionEvent() {
		ConfigNode selectedWorkList = worklistNodeSelector.getValue();
		fireEvent(new WorkListSelectionEvent(this, false, selectedWorkList));
	}

	@Getter
	public static class WorkListSelectionEvent extends ComponentEvent<DicomWorkListSelectionDialog> {

		private final ConfigNode selectedWorkList;

		public WorkListSelectionEvent(DicomWorkListSelectionDialog source, boolean fromClient,
				ConfigNode selectedWorkList) {
			super(source, fromClient);

			this.selectedWorkList = selectedWorkList;
		}

	}

}
