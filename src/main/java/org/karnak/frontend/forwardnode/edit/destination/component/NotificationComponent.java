/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.forwardnode.edit.destination.component;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.constant.DefaultValuesNotification;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.frontend.component.converter.HStringToIntegerConverter;
import org.karnak.frontend.util.UIS;

/** Create a notification component */
public class NotificationComponent extends VerticalLayout {

  // Components
  private TextField notify;
  private TextField notifyObjectErrorPrefix;
  private TextField notifyObjectPattern;
  private TextField notifyObjectValues;
  private TextField notifyInterval;
  private Checkbox activateNotification;
  private Div notificationInputsDiv;

  /** Constructor */
  public NotificationComponent() {
    // Size
    setWidthFull();

    // In order to not have a padding around the component
    setPadding(false);

    // Build notification components
    buildComponents();

    // Build listeners
    buildListeners();

    // Add components
    addComponents();
  }

  /** Add components in notification components */
  private void addComponents() {
    notificationInputsDiv.add(
        UIS.setWidthFull(new HorizontalLayout(notify)),
        UIS.setWidthFull(
            new HorizontalLayout(
                notifyObjectErrorPrefix, notifyObjectPattern, notifyObjectValues, notifyInterval)));

    add(
        UIS.setWidthFull(new HorizontalLayout(activateNotification)),
        UIS.setWidthFull(notificationInputsDiv));
  }

  /** Build listeners on components */
  private void buildListeners() {
    buildListenerActivateNotification();
  }

  /** Listener activate notification */
  private void buildListenerActivateNotification() {
    activateNotification.addValueChangeListener(
        event -> {
          if (event != null && event.getValue()) {
            notificationInputsDiv.setVisible(true);
            notify.clear();
            notifyObjectErrorPrefix.setValue(DefaultValuesNotification.OBJECT_ERROR_PREFIX);
            notifyObjectPattern.setValue(DefaultValuesNotification.OBJECT_PATTERN);
            notifyObjectValues.setValue(DefaultValuesNotification.OBJECT_VALUES);
            notifyInterval.setValue(DefaultValuesNotification.INTERVAL);
          } else {
            notificationInputsDiv.setVisible(false);
            notify.clear();
            notifyObjectErrorPrefix.clear();
            notifyObjectPattern.clear();
            notifyObjectValues.clear();
            notifyInterval.clear();
          }
        });
  }

  /** Build components used in Notification component */
  private void buildComponents() {
    buildNotificationInputsDiv();
    buildActivateNotification();
    buildNotify();
    buildNotifyObjectErrorPrefix();
    buildNotifyObjectPattern();
    buildNotifyObjectValues();
    buildNotifyInterval();
  }

  /** Notify interval */
  private void buildNotifyInterval() {
    notifyInterval = new TextField("Notif.: interval");
    notifyInterval.setWidth("18%");
    notifyInterval.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
    UIS.setTooltip(
        notifyInterval,
        "Interval in seconds for sending a notification (when no new image is arrived in the archive folder). Default value: 45");
  }

  /** Notify Object Values */
  private void buildNotifyObjectValues() {
    notifyObjectValues = new TextField("Notif.: subject values");
    notifyObjectValues.setWidth("24%");
    UIS.setTooltip(
        notifyObjectValues,
        "Values injected in the pattern [PatientID StudyDescription StudyDate StudyInstanceUID]. Default value: PatientID,StudyDescription");
  }

  /** Notify Object Pattern */
  private void buildNotifyObjectPattern() {
    notifyObjectPattern = new TextField("Notif.: subject pattern");
    notifyObjectPattern.setWidth("24%");
    UIS.setTooltip(
        notifyObjectPattern,
        "Pattern of the email object, see https://dzone.com/articles/java-string-format-examples. Default value: [Karnak Notification] %s %.30s");
  }

  /** Notify Object Error Prefix */
  private void buildNotifyObjectErrorPrefix() {
    notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
    notifyObjectErrorPrefix.setWidth("24%");
    UIS.setTooltip(
        notifyObjectErrorPrefix,
        "Prefix of the email object when containing an issue. Default value: **ERROR**");
  }

  /** Notify */
  private void buildNotify() {
    notify = new TextField("Notif.: list of emails");
    notify.setWidth("100%");
    notify.getStyle().set("padding-top", "0");
    notify.getStyle().set("padding", "0");
  }

  /** Activate Notification */
  private void buildActivateNotification() {
    activateNotification = new Checkbox("Activate notification");
    // By default deactivate
    activateNotification.setValue(false);
  }

  /** Notification Inputs Div */
  private void buildNotificationInputsDiv() {
    notificationInputsDiv = new Div();
    // By default hide
    notificationInputsDiv.setVisible(false);
  }

  /**
   * Init binder for the component
   *
   * @param binder Binder
   */
  public void init(Binder<DestinationEntity> binder) {

    // Activate notification
    binder
        .forField(getActivateNotification())
        .bind(
            DestinationEntity::isActivateNotification, DestinationEntity::setActivateNotification);

    // List of emails
    binder
        .forField(getNotify())
        .withValidator(
            (s, valueContext) -> {
              if (StringUtils.isBlank(s) && getActivateNotification().getValue()) {
                return ValidationResult.error("Should have at least one address email");
              }
              return ValidationResult.ok();
            })
        .bind(DestinationEntity::getNotify, DestinationEntity::setNotify);

    // Interval
    binder
        .forField(getNotifyInterval()) //
        .withConverter(new HStringToIntegerConverter()) //
        .bind(DestinationEntity::getNotifyInterval, DestinationEntity::setNotifyInterval);

    // Error Prefix
    binder
        .forField(getNotifyObjectErrorPrefix())
        .bind(
            DestinationEntity::getNotifyObjectErrorPrefix,
            DestinationEntity::setNotifyObjectErrorPrefix);

    // Subject Pattern
    binder
        .forField(getNotifyObjectPattern())
        .bind(DestinationEntity::getNotifyObjectPattern, DestinationEntity::setNotifyObjectPattern);

    // Subject Values
    binder
        .forField(getNotifyObjectValues())
        .bind(DestinationEntity::getNotifyObjectValues, DestinationEntity::setNotifyObjectValues);
  }

  public TextField getNotify() {
    return notify;
  }

  public void setNotify(TextField notify) {
    this.notify = notify;
  }

  public TextField getNotifyObjectErrorPrefix() {
    return notifyObjectErrorPrefix;
  }

  public void setNotifyObjectErrorPrefix(TextField notifyObjectErrorPrefix) {
    this.notifyObjectErrorPrefix = notifyObjectErrorPrefix;
  }

  public TextField getNotifyObjectPattern() {
    return notifyObjectPattern;
  }

  public void setNotifyObjectPattern(TextField notifyObjectPattern) {
    this.notifyObjectPattern = notifyObjectPattern;
  }

  public TextField getNotifyObjectValues() {
    return notifyObjectValues;
  }

  public void setNotifyObjectValues(TextField notifyObjectValues) {
    this.notifyObjectValues = notifyObjectValues;
  }

  public TextField getNotifyInterval() {
    return notifyInterval;
  }

  public void setNotifyInterval(TextField notifyInterval) {
    this.notifyInterval = notifyInterval;
  }

  public Checkbox getActivateNotification() {
    return activateNotification;
  }

  public void setActivateNotification(Checkbox activateNotification) {
    this.activateNotification = activateNotification;
  }
}
