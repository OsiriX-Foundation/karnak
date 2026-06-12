/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Model used to build transfer monitoring notification
 */
@Setter
@Getter
public class TransferMonitoringNotification {

	private String subject;

	private String from;

	private String to;

	private String patientId;

	private String studyUid;

	private String accessionNumber;

	private String studyDescription;

	private LocalDateTime studyDate;

	private String source;

	private String destination;

	private List<SerieSummaryNotification> serieSummaryNotifications;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TransferMonitoringNotification that = (TransferMonitoringNotification) o;
		return Objects.equals(subject, that.subject) && Objects.equals(from, that.from) && Objects.equals(to, that.to)
				&& Objects.equals(patientId, that.patientId) && Objects.equals(studyUid, that.studyUid)
				&& Objects.equals(accessionNumber, that.accessionNumber)
				&& Objects.equals(studyDescription, that.studyDescription) && Objects.equals(studyDate, that.studyDate)
				&& Objects.equals(source, that.source) && Objects.equals(destination, that.destination)
				&& Objects.equals(serieSummaryNotifications, that.serieSummaryNotifications);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subject, from, to, patientId, studyUid, accessionNumber, studyDescription, studyDate,
				source, destination, serieSummaryNotifications);
	}

}
