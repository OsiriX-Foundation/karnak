/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.frontend.project;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.frontend.component.WarningConfirmDialog;

public class ProjectSecret extends Div {

  private final String WARNING_TEXT =
      "If you change the project secret, the integrity of the DICOM will be compromise";
  private final String REFER_LINK_TEXT =
      "For more details on the use of the project secret, please refer to the following link";
  private final Anchor REFER_LINK =
      new Anchor(
          "https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/rules#action-u-generate-a-new-uid",
          "How KARNAK does ?");

  private final Div titleDiv = new Div();
  private final Div valueDiv = new Div();
  private final Div messageWarningLayout = new Div();
  private final TextField textProjectSecret;
  private final Button generateButton = new Button("Generate Secret");

  private final String TITLE = "Project Secret";

  public ProjectSecret(TextField textProjectSecret) {
    this.textProjectSecret = textProjectSecret;

    setWidthFull();
    setTitle();
    setValue();
    setMessageWarningLayout();
    eventGenerateSecret();
    add(titleDiv, valueDiv);
  }

  private void setTitle() {
    titleDiv.setText(TITLE);
  }

  private void setValue() {
    textProjectSecret.getStyle().set("width", "80%");
    generateButton.getStyle().set("margin-left", "10px");
    valueDiv.add(textProjectSecret, generateButton);
  }

  private void setMessageWarningLayout() {
    messageWarningLayout.add(new Div(new Text(WARNING_TEXT)));
    messageWarningLayout.add(new Div(new Text(REFER_LINK_TEXT)));
    messageWarningLayout.add(REFER_LINK);
  }

  public void clear() {
    textProjectSecret.clear();
  }

  private void eventGenerateSecret() {
    generateButton.addClickListener(
        event -> {
          WarningConfirmDialog dialog = new WarningConfirmDialog(messageWarningLayout);
          dialog.addConfirmationListener(
              componentEvent -> {
                String generateSecret = HMAC.byteToHex(HMAC.generateRandomKey());
                textProjectSecret.setValue(HMAC.showHexKey(generateSecret));
              });
          dialog.open();
        });
  }
}
