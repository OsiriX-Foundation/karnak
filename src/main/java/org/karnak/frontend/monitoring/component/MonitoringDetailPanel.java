/*
 * Copyright (c) 2022-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.monitoring.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.util.DateFormat;
import org.karnak.frontend.monitoring.component.MonitoringNode.DestinationNode;
import org.karnak.frontend.monitoring.component.MonitoringNode.ErrorNode;
import org.karnak.frontend.monitoring.component.MonitoringNode.SeriesNode;
import org.karnak.frontend.monitoring.component.MonitoringNode.StudyNode;
import org.weasis.core.util.annotations.Generated;

/**
 * Detail panel beside the monitoring tree: shows the full set of fields for the selected
 * destination / study / series / error as read-only (so values can be selected and
 * copied), plus a "Copy" button that copies the whole block to the clipboard.
 */
@Generated()
public class MonitoringDetailPanel extends VerticalLayout {

	private record Field(String label, String value, boolean multiline) {
	}

	private final Span title = new Span("Details");

	private final Button copyButton = new Button("Copy", VaadinIcon.COPY.create());

	private final FormLayout form = new FormLayout();

	private final Span placeholder = new Span("Select a destination, study, series or error to see its details.");

	private transient String copyText = "";

	public MonitoringDetailPanel() {
		title.getStyle().set("font-weight", "600").set("font-size", "var(--lumo-font-size-l)");
		copyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
		copyButton.setEnabled(false);
		copyButton.addClickListener(event -> copyToClipboard());

		placeholder.getStyle().set("color", "var(--lumo-secondary-text-color)");
		form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
		form.setWidthFull();

		// The fields scroll while the Copy button stays visible at the bottom
		VerticalLayout body = new VerticalLayout(placeholder, form);
		body.setPadding(false);
		body.setSpacing(true);
		body.setWidthFull();
		Scroller scroller = new Scroller(body);
		scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
		scroller.setSizeFull();

		HorizontalLayout footer = new HorizontalLayout(copyButton);
		footer.setWidthFull();
		footer.setJustifyContentMode(JustifyContentMode.END);

		setSizeFull();
		setPadding(true);
		setSpacing(true);
		add(title, scroller, footer);
		setFlexGrow(1, scroller);
	}

	/**
	 * Render the details of the selected node, or the placeholder when nothing is
	 * selected.
	 */
	public void show(MonitoringNode node) {
		form.removeAll();
		if (node == null) {
			title.setText("Details");
			placeholder.setVisible(true);
			copyButton.setEnabled(false);
			copyText = "";
			return;
		}
		placeholder.setVisible(false);
		title.setText(titleFor(node));

		List<Field> fields = fieldsFor(node);
		StringBuilder copyBuilder = new StringBuilder(title.getText()).append('\n');
		for (Field field : fields) {
			form.addFormItem(readOnly(field), field.label());
			copyBuilder.append(field.label()).append(": ").append(field.value()).append('\n');
		}
		copyText = copyBuilder.toString();
		copyButton.setEnabled(true);
	}

	private com.vaadin.flow.component.Component readOnly(Field field) {
		if (field.multiline()) {
			TextArea area = new TextArea();
			area.setValue(field.value());
			area.setReadOnly(true);
			area.setWidthFull();
			return area;
		}
		TextField textField = new TextField();
		textField.setValue(field.value());
		textField.setReadOnly(true);
		textField.setWidthFull();
		return textField;
	}

	private String titleFor(MonitoringNode node) {
		return switch (node) {
			case DestinationNode d -> d.displayName();
			case StudyNode s -> "Study " + StringUtils.defaultString(s.studyUid());
			case SeriesNode se -> "Series " + StringUtils.defaultString(se.serieUid());
			case ErrorNode ignored -> "Error";
		};
	}

	private List<Field> fieldsFor(MonitoringNode node) {
		List<Field> fields = new ArrayList<>();
		switch (node) {
			case DestinationNode d -> {
				text(fields, "Forward AET", d.forwardAet());
				text(fields, "Destination", d.destinationLabel());
				number(fields, "Studies", d.studies());
				number(fields, "Series", d.series());
				number(fields, "Instances", d.instances());
				number(fields, "Sent", d.sent());
				number(fields, "Errors", d.errors());
			}
			case StudyNode s -> {
				text(fields, "Study UID", s.studyUid());
				deidentified(fields, "Study UID", s.studyUid(), s.studyUidToSend());
				text(fields, "Patient ID", s.patientIdOriginal());
				deidentified(fields, "Patient ID", s.patientIdOriginal(), s.patientIdToSend());
				text(fields, "Accession number", s.accessionNumberOriginal());
				deidentified(fields, "Accession number", s.accessionNumberOriginal(), s.accessionNumberToSend());
				text(fields, "Description", s.description());
				date(fields, "Study date", s.studyDateOriginal());
				number(fields, "Series", s.series());
				number(fields, "Instances", s.instances());
				number(fields, "Sent", s.sent());
				number(fields, "Errors", s.errors());
				date(fields, "First seen", s.firstSeen());
				date(fields, "Last seen", s.lastSeen());
			}
			case SeriesNode se -> {
				text(fields, "Series UID", se.serieUid());
				deidentified(fields, "Series UID", se.serieUid(), se.serieUidToSend());
				text(fields, "Description", se.description());
				text(fields, "Modality", se.modality());
				text(fields, "SOP classes", se.sopClassUids());
				date(fields, "Series date", se.serieDateOriginal());
				number(fields, "Instances", se.instances());
				number(fields, "Sent", se.sent());
				number(fields, "Errors", se.errors());
				date(fields, "First seen", se.firstSeen());
				date(fields, "Last seen", se.lastSeen());
			}
			case ErrorNode e -> {
				fields.add(new Field("Reason", StringUtils.defaultString(e.reason()), true));
				number(fields, "Instances affected", e.instances());
			}
		}
		return fields;
	}

	private void text(List<Field> fields, String label, String value) {
		if (StringUtils.isNotBlank(value)) {
			fields.add(new Field(label, value, false));
		}
	}

	private void number(List<Field> fields, String label, long value) {
		fields.add(new Field(label, Long.toString(value), false));
	}

	private void date(List<Field> fields, String label, LocalDateTime value) {
		if (value != null) {
			fields.add(
					new Field(label, DateFormat.format(value, DateFormat.FORMAT_DDMMYYYY_SLASH_HHMMSS_2POINTS), false));
		}
	}

	/**
	 * Adds the de-identified value only when it is present and differs from the original.
	 */
	private void deidentified(List<Field> fields, String label, String original, String toSend) {
		if (StringUtils.isNotBlank(toSend) && !Objects.equals(original, toSend)) {
			fields.add(new Field(label + " (de-identified)", toSend, false));
		}
	}

	private void copyToClipboard() {
		copyButton.getElement().executeJs("navigator.clipboard.writeText($0).then(() => {}, () => {})", copyText);
		Notification notification = Notification.show("Details copied to clipboard");
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification.setDuration(2000);
		notification.setPosition(Position.MIDDLE);
	}

}
