/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.enums;

/** Enum for the transfer status */
public enum TransferStatusType {

	ALL(null, "All"), SENT(true, "Sent"), NOT_SENT(false, "Not Sent");

	/** Code of the enum */
	private final Boolean code;

	/** Description of the enum */
	private final String description;

	/**
	 * Constructor
	 * @param code Code
	 * @param description Description
	 */
	TransferStatusType(Boolean code, String description) {
		this.code = code;
		this.description = description;
	}

	public Boolean getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
