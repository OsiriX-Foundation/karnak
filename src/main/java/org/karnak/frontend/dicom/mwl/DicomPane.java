/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.dicom.mwl;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.StreamResource;
import org.dcm4che6.data.DicomObject;

public class DicomPane extends Composite<Dialog> {

  private static final long serialVersionUID = 1L;

  // CONTROLLER
  private final DicomPaneLogic logic = new DicomPaneLogic(this);
  // DATA
  private final DicomObject dcm;
  // UI COMPONENTS
  private Dialog currentDialog;
  private VerticalLayout mainLayout;
  private Div titleBar;
  private TextArea contentFld;
  private HorizontalLayout buttonBar;
  private Anchor downloadDicomAnchor;
  private Anchor downloadTextAnchor;

  public DicomPane(DicomObject dcm) {
    this.dcm = dcm;

    init();

    buildMainLayout();

    currentDialog.add(mainLayout);
  }

  public void open() {
    currentDialog.open();
  }

  private void init() {
    currentDialog = getContent();
    currentDialog.setSizeFull();
  }

  private void buildMainLayout() {
    mainLayout = new VerticalLayout();
    mainLayout.setSizeFull();
    mainLayout.setPadding(false);

    buildTitleBar();
    buildContentField();
    buildButtonBar();

    mainLayout.add(titleBar, contentFld, buttonBar);

    mainLayout.setFlexGrow(1, contentFld);
  }

  private void buildTitleBar() {
    titleBar = new Div();
    titleBar.setText("Worklist Entry");
  }

  private void buildContentField() {
    contentFld = new TextArea();

    contentFld.setReadOnly(true);
    contentFld.setHeight("600px");
    contentFld.setWidth("600px");

    contentFld.setValue(dcm.toString(1500, 300));
  }

  private void buildButtonBar() {
    buttonBar = new HorizontalLayout();

    buildDownloadTextAnchor();
    buildDownloadDicomAnchor();

    buttonBar.add(downloadDicomAnchor, downloadTextAnchor);
  }

  private void buildDownloadTextAnchor() {
    Button downloadTextBtn = new Button();
    downloadTextBtn.setText("Download Text");

    downloadTextAnchor =
        new Anchor(
            new StreamResource("worklistItem.txt", () -> logic.getWorklistItemInputStreamText(dcm)),
            "");
    downloadTextAnchor.getElement().setAttribute("download", true);
    downloadTextAnchor.add(downloadTextBtn);
  }

  private void buildDownloadDicomAnchor() {
    Button downloadDicomBtn = new Button();
    downloadDicomBtn.setText("Download DICOM");

    downloadDicomAnchor =
        new Anchor(
            new StreamResource(
                "worklistItem.dcm", () -> logic.getWorklistItemInputStreamInDicom(dcm)),
            "");
    downloadDicomAnchor.getElement().setAttribute("download", true);
    downloadDicomAnchor.add(downloadDicomBtn);
  }
}
