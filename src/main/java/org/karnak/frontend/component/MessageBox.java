/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.component;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.karnak.backend.enums.MessageLevel;
import org.karnak.backend.enums.MessageType;
import org.karnak.backend.model.dicom.Message;

// @JsModule("@polymer/iron-icons/iron-icons.js")
public class MessageBox extends Composite<Div> {

  private static final long serialVersionUID = 1L;

  // UI COMPONENTS
  private HorizontalLayout layout;
  private Div titleDiv;
  private Div contentDiv;
  private IronIcon icon;

  // DATA
  private Message message;
  private final MessageType type;

  public MessageBox(MessageType type) {
    this.type = type;

    createMessageBox();
    createLayout();
    createTitleDiv();
    createContentDiv();

    layout.add(titleDiv, contentDiv);

    getContent().add(layout);
  }

  public MessageBox(Message message, MessageType type) {
    this.message = message;
    this.type = type;

    createMessageBox();
    createLayout();
    createTitleDiv();
    createContentDiv();

    layout.add(titleDiv, contentDiv);

    getContent().add(layout);
  }

  public void setMessage(Message message) {
    this.message = message;

    updateLayout();
    updateTitleDiv();
    updateContentDiv();
  }

  private void createMessageBox() {
    getContent().addClassName("message-box");
  }

  private void createLayout() {
    layout = new HorizontalLayout();
    layout.addClassName("message-box-layout");
    if (type == MessageType.STATIC_MESSAGE) {
      layout.addClassName("message-box-layout-with-margin");
    }

    if (message != null) {
      if (MessageLevel.INFO == message.getLevel()) {
        layout.addClassName("info");
      } else if (MessageLevel.WARN == message.getLevel()) {
        layout.addClassName("warn");
      } else if (MessageLevel.ERROR == message.getLevel()) {
        layout.addClassName("error");
      }
    }
  }

  private void createTitleDiv() {
    titleDiv = new Div();
    titleDiv.addClassName("message-box-title");

    if (message != null) {
      if (MessageLevel.INFO == message.getLevel()) {
        titleDiv.addClassName("info");
      } else if (MessageLevel.WARN == message.getLevel()) {
        titleDiv.addClassName("warn");
      } else if (MessageLevel.ERROR == message.getLevel()) {
        titleDiv.addClassName("error");
      }

      createIcon();
      titleDiv.add(icon);
    }
  }

  private void createContentDiv() {
    contentDiv = new Div();
    contentDiv.addClassName("message-box-content");

    if (message != null) {
      switch (message.getFormat()) {
        case TEXT:
          contentDiv.setText(message.getText());
          break;
        case HTML:
          contentDiv.removeAll();
          contentDiv.add(new Html("<span>" + message.getText() + "</span>"));
          break;
        default:
          break;
      }
    }
  }

  private void createIcon() {
    if (message != null) {
      if (MessageLevel.INFO == message.getLevel()) {
        icon = new IronIcon("icons", "info-outline");
      } else if (MessageLevel.WARN == message.getLevel()) {
        icon = new IronIcon("icons", "warning");
      } else if (MessageLevel.ERROR == message.getLevel()) {
        icon = new IronIcon("icons", "error-outline");
      }
    }
  }

  private void updateLayout() {
    if (message != null) {
      if (MessageLevel.INFO == message.getLevel()) {
        layout.addClassName("info");
      } else if (MessageLevel.WARN == message.getLevel()) {
        layout.addClassName("warn");
      } else if (MessageLevel.ERROR == message.getLevel()) {
        layout.addClassName("error");
      }
    }
  }

  private void updateTitleDiv() {
    if (message != null) {
      if (MessageLevel.INFO == message.getLevel()) {
        titleDiv.addClassName("info");
      } else if (MessageLevel.WARN == message.getLevel()) {
        titleDiv.addClassName("warn");
      } else if (MessageLevel.ERROR == message.getLevel()) {
        titleDiv.addClassName("error");
      }

      createIcon();
      titleDiv.add(icon);
    }
  }

  private void updateContentDiv() {
    if (message != null) {
      switch (message.getFormat()) {
        case TEXT:
          contentDiv.setText(message.getText());
          break;
        case HTML:
          contentDiv.removeAll();
          contentDiv.add(new Html("<span>" + message.getText() + "</span>"));
          break;
        default:
          break;
      }
    }
  }
}
